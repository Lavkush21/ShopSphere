
package com.shopsphere.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.Customer;
import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.repository.CustomerOrderRepository;
import com.shopsphere.repository.CustomerRepository;
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

    public CustomerOrderServiceImpl(
            CustomerOrderRepository orderRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    // Create Order
    @Override
    public CustomerOrder createOrder(CustomerOrder order, Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        return orderRepository.save(order);
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
    public CustomerOrder updateOrder(Long id, CustomerOrder order) {

        if (order.getStatus() == null) {
            throw new RuntimeException("Order status cannot be empty");
        }

        return updateOrderStatus(id, order.getStatus());
    }

    // Update Order Status With Validation
    @Override
    public CustomerOrder updateOrderStatus(Long id, String status) {

        CustomerOrder order = getOrderById(id);

        if (status == null || status.trim().isEmpty()) {
            throw new RuntimeException("Order status cannot be empty");
        }

        String normalizedStatus = status.trim().toUpperCase();

        if (!VALID_STATUSES.contains(normalizedStatus)) {
            throw new RuntimeException(
                    "Invalid order status. Allowed statuses: "
                    + VALID_STATUSES);
        }

        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("Cannot update a cancelled order");
        }

        order.setStatus(normalizedStatus);

        return orderRepository.save(order);
    }

    // Cancel Order and Restore Product Stock
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
                    "Cannot cancel shipped or delivered order");
        }

        // Restore Product Stock
        for (OrderItem item : order.getOrderItems()) {

            var product = item.getProduct();

            product.setStock(product.getStock() + item.getQuantity());

            productRepository.save(product);
        }

        order.setStatus("CANCELLED");

        return orderRepository.save(order);
    }

    // Delete Order
    @Override
    public void deleteOrder(Long id) {

        CustomerOrder order = getOrderById(id);

        orderRepository.delete(order);
    }
}
