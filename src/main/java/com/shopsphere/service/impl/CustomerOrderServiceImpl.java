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
import com.shopsphere.entity.Product;

import com.shopsphere.repository.CustomerOrderRepository;
import com.shopsphere.repository.CustomerRepository;
import com.shopsphere.repository.OrderStatusHistoryRepository;
import com.shopsphere.repository.ProductRepository;

import com.shopsphere.service.CustomerOrderService;

@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private static final Set<String> VALID_STATUSES = Set.of(
            "PENDING",
            "CONFIRMED",
            "SHIPPED",
            "DELIVERED",
            "CANCELLED"
    );

    private final CustomerOrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderStatusHistoryRepository historyRepository;

    public CustomerOrderServiceImpl(
            CustomerOrderRepository orderRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            OrderStatusHistoryRepository historyRepository) {

        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.historyRepository = historyRepository;
    }

    // Create Order
    @Override
    public CustomerOrder createOrder(CustomerOrder order, Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        CustomerOrder savedOrder = orderRepository.save(order);

        saveStatusHistory(
                savedOrder,
                null,
                "PENDING",
                "Order created"
        );

        return savedOrder;
    }

    // Get All Orders
    @Override
    public List<CustomerOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get Order By ID
    @Override
    public CustomerOrder getOrderById(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // Get Orders By Customer ID
    @Override
    public List<CustomerOrder> getOrdersByCustomerId(Long customerId) {

        return orderRepository.findByCustomer_Id(customerId);
    }

    // Update Order
    @Override
    public CustomerOrder updateOrder(Long id, CustomerOrder updatedOrder) {

        CustomerOrder existingOrder = getOrderById(id);

        if (updatedOrder.getStatus() == null) {
            throw new RuntimeException("Order status cannot be null");
        }

        return updateOrderStatus(id, updatedOrder.getStatus());
    }

    // Update Order Status
    @Override
    public CustomerOrder updateOrderStatus(Long id, String status) {

        CustomerOrder order = getOrderById(id);

        if (status == null || status.isBlank()) {
            throw new RuntimeException("Order status cannot be empty");
        }

        String newStatus = status.trim().toUpperCase();

        if (!VALID_STATUSES.contains(newStatus)) {
            throw new RuntimeException(
                    "Invalid order status. Allowed statuses: " + VALID_STATUSES
            );
        }

        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("Cancelled order cannot be updated");
        }

        String previousStatus = order.getStatus();

        if (!previousStatus.equals(newStatus)) {

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

        return order;
    }

    // Cancel Order
    @Override
    @Transactional
    public CustomerOrder cancelOrder(Long id) {

        CustomerOrder order = getOrderById(id);

        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("Order is already cancelled");
        }

        if ("SHIPPED".equals(order.getStatus())
                || "DELIVERED".equals(order.getStatus())) {

            throw new RuntimeException(
                    "Shipped or delivered order cannot be cancelled"
            );
        }

        String previousStatus = order.getStatus();

        // Restore product stock
        if (order.getOrderItems() != null) {

            for (OrderItem item : order.getOrderItems()) {

                Product product = item.getProduct();

                product.setStock(
                        product.getStock() + item.getQuantity()
                );

                productRepository.save(product);
            }
        }

        order.setStatus("CANCELLED");

        CustomerOrder savedOrder = orderRepository.save(order);

        saveStatusHistory(
                savedOrder,
                previousStatus,
                "CANCELLED",
                "Order cancelled and product stock restored"
        );

        return savedOrder;
    }

    // Delete Order
    @Override
    public void deleteOrder(Long id) {

        CustomerOrder order = getOrderById(id);
        orderRepository.delete(order);
    }

    // Save Order Status History
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