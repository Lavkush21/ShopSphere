package com.shopsphere.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.Customer;
import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.OrderStatusHistory;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.CustomerOrderRepository;
import com.shopsphere.repository.CustomerRepository;
import com.shopsphere.repository.OrderStatusHistoryRepository;
import com.shopsphere.service.CustomerOrderService;
import com.shopsphere.service.InventoryService;
import com.shopsphere.service.OrderEmailNotificationService;

@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private static final Set<String> VALID_STATUSES = Set.of(
            "PENDING",
            "CONFIRMED",
            "SHIPPED",
            "DELIVERED",
            "CANCELLED",
            "RETURNED"
    );

    private final CustomerOrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final OrderEmailNotificationService orderEmailNotificationService;
    private final InventoryService inventoryService;

    public CustomerOrderServiceImpl(
            CustomerOrderRepository orderRepository,
            CustomerRepository customerRepository,
            OrderStatusHistoryRepository historyRepository,
            OrderEmailNotificationService orderEmailNotificationService,
            InventoryService inventoryService) {

        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.historyRepository = historyRepository;
        this.orderEmailNotificationService = orderEmailNotificationService;
        this.inventoryService = inventoryService;
    }

    @Override
    public CustomerOrder createOrder(CustomerOrder order, Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + customerId));

        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        CustomerOrder savedOrder = orderRepository.save(order);

        saveStatusHistory(savedOrder, null, "PENDING", "Order created");

        try {
            orderEmailNotificationService.sendOrderConfirmation(
                    customer.getEmail(),
                    savedOrder.getId(),
                    savedOrder.getTotalAmount()
            );
        } catch (Exception e) {
            System.err.println(
                    "Order confirmation email failed: " + e.getMessage());
        }

        return savedOrder;
    }

    @Override
    public List<CustomerOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public CustomerOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + id));
    }

    @Override
    public List<CustomerOrder> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomer_Id(customerId);
    }

    @Override
    public CustomerOrder updateOrder(Long id, CustomerOrder updatedOrder) {

        if (updatedOrder.getStatus() == null) {
            throw new RuntimeException("Order status cannot be null");
        }

        return updateOrderStatus(id, updatedOrder.getStatus());
    }

    @Override
    public CustomerOrder updateOrderStatus(Long id, String status) {

        CustomerOrder order = getOrderById(id);

        if (status == null || status.isBlank()) {
            throw new RuntimeException("Order status cannot be empty");
        }

        String newStatus = status.trim().toUpperCase();
        String previousStatus = order.getStatus();

        if (!VALID_STATUSES.contains(newStatus)) {
            throw new RuntimeException("Invalid order status");
        }

        if (previousStatus.equals(newStatus)) {
            return order;
        }

        if ("CANCELLED".equals(previousStatus)
                || "RETURNED".equals(previousStatus)) {
            throw new RuntimeException(
                    "Cancelled or returned order cannot be updated");
        }

        if ("RETURNED".equals(newStatus)) {
            throw new RuntimeException(
                    "Order can be marked RETURNED only through return completion");
        }

        if (!isValidTransition(previousStatus, newStatus)) {
            throw new RuntimeException(
                    "Invalid order status transition: "
                    + previousStatus + " to " + newStatus);
        }

        order.setStatus(newStatus);

        CustomerOrder savedOrder = orderRepository.save(order);

        saveStatusHistory(
                savedOrder,
                previousStatus,
                newStatus,
                "Order status updated"
        );

        return savedOrder;
    }

    private boolean isValidTransition(String currentStatus, String newStatus) {

        switch (currentStatus) {
            case "PENDING":
                return "CONFIRMED".equals(newStatus);

            case "CONFIRMED":
                return "SHIPPED".equals(newStatus);

            case "SHIPPED":
                return "DELIVERED".equals(newStatus);

            case "DELIVERED":
                return false;

            default:
                return false;
        }
    }

    @Override
    @Transactional
    public CustomerOrder cancelOrder(Long id) {

        CustomerOrder order = getOrderById(id);

        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("Order is already cancelled");
        }

        if (!"PENDING".equals(order.getStatus())
                && !"CONFIRMED".equals(order.getStatus())) {
            throw new RuntimeException(
                    "Only pending or confirmed orders can be cancelled");
        }

        String previousStatus = order.getStatus();

        restoreInventoryStock(order, "ORDER_CANCELLATION");

        order.setStatus("CANCELLED");

        CustomerOrder savedOrder = orderRepository.save(order);

        saveStatusHistory(
                savedOrder,
                previousStatus,
                "CANCELLED",
                "Order cancelled and inventory stock restored"
        );

        return savedOrder;
    }

    @Override
    @Transactional
    public CustomerOrder completeReturn(Long id) {

        CustomerOrder order = getOrderById(id);

        if (!"DELIVERED".equals(order.getStatus())) {
            throw new RuntimeException(
                    "Only delivered orders can be returned");
        }

        String previousStatus = order.getStatus();

        restoreInventoryStock(order, "RETURN_COMPLETED");

        order.setStatus("RETURNED");

        CustomerOrder savedOrder = orderRepository.save(order);

        saveStatusHistory(
                savedOrder,
                previousStatus,
                "RETURNED",
                "Return completed and inventory stock restored"
        );

        return savedOrder;
    }

    private void restoreInventoryStock(CustomerOrder order, String reason) {

        if (order.getOrderItems() != null) {

            for (OrderItem item : order.getOrderItems()) {

                Long productId = item.getProduct().getId();
                Integer quantity = item.getQuantity();

                inventoryService.adjustStock(
                        productId,
                        quantity,
                        reason
                );
            }
        }
    }

    @Override
    public void deleteOrder(Long id) {

        CustomerOrder order = getOrderById(id);
        orderRepository.delete(order);
    }

    private void saveStatusHistory(
            CustomerOrder order,
            String previousStatus,
            String newStatus,
            String remarks) {

        OrderStatusHistory history = new OrderStatusHistory();

        history.setOrder(order);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setStatusChangedAt(LocalDateTime.now());
        history.setRemarks(remarks);

        historyRepository.save(history);
    }
}