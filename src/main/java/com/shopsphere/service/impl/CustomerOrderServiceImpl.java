
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
            "PENDING", "CONFIRMED", "SHIPPED",
            "DELIVERED", "CANCELLED"
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

    @Override
    public CustomerOrder createOrder(CustomerOrder order, Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        return orderRepository.save(order);
    }

    @Override
    public List<CustomerOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public CustomerOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<CustomerOrder> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomer_Id(customerId);
    }

    @Override
    public CustomerOrder updateOrder(Long id, CustomerOrder order) {

        CustomerOrder existingOrder = getOrderById(id);

        existingOrder.setStatus(order.getStatus());

        return orderRepository.save(existingOrder);
    }

    @Override
    public CustomerOrder updateOrderStatus(Long id, String status) {

        CustomerOrder order = getOrderById(id);

        if (status == null || !VALID_STATUSES.contains(status.toUpperCase())) {
            throw new RuntimeException("Invalid order status");
        }

        order.setStatus(status.toUpperCase());

        return orderRepository.save(order);
    }

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

        // Restore product stock
        for (OrderItem item : order.getOrderItems()) {

            var product = item.getProduct();

            product.setStock(product.getStock() + item.getQuantity());

            productRepository.save(product);
        }

        order.setStatus("CANCELLED");

        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {

        CustomerOrder order = getOrderById(id);

        orderRepository.delete(order);
    }
}
