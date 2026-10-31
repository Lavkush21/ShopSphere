package com.shopsphere.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shopsphere.entity.Customer;
import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.repository.CustomerOrderRepository;
import com.shopsphere.repository.CustomerRepository;
import com.shopsphere.service.CustomerOrderService;

@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private final CustomerOrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public CustomerOrderServiceImpl(
            CustomerOrderRepository orderRepository,
            CustomerRepository customerRepository) {

        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerOrder createOrder(CustomerOrder order, Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        order.setCustomer(customer);
        order.setOrderNumber("ORD-" + System.currentTimeMillis());
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public List<CustomerOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public CustomerOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Override
    public List<CustomerOrder> getOrdersByCustomerId(Long customerId) {

        customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return orderRepository.findByCustomer_Id(customerId);
    }

    @Override
    public CustomerOrder updateOrder(Long id, CustomerOrder order) {

        CustomerOrder existingOrder = getOrderById(id);

        existingOrder.setTotalAmount(order.getTotalAmount());
        existingOrder.setStatus(order.getStatus());

        return orderRepository.save(existingOrder);
    }

    @Override
    public CustomerOrder updateOrderStatus(Long id, String status) {

        CustomerOrder order = getOrderById(id);
        order.setStatus(status);

        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        CustomerOrder order = getOrderById(id);
        orderRepository.delete(order);
    }
}