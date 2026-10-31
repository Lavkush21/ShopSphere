package com.shopsphere.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.service.CustomerOrderService;

@RestController
@RequestMapping("/api/orders")
public class CustomerOrderController {

    private final CustomerOrderService orderService;

    public CustomerOrderController(CustomerOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public CustomerOrder createOrder(
            @RequestBody CustomerOrder order,
            @RequestParam Long customerId) {

        return orderService.createOrder(order, customerId);
    }

    @GetMapping
    public List<CustomerOrder> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public CustomerOrder getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<CustomerOrder> getOrdersByCustomerId(
            @PathVariable Long customerId) {

        return orderService.getOrdersByCustomerId(customerId);
    }

    @PutMapping("/{id}")
    public CustomerOrder updateOrder(
            @PathVariable Long id,
            @RequestBody CustomerOrder order) {
        return orderService.updateOrder(id, order);
    }

    @PutMapping("/{id}/status")
    public CustomerOrder updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return orderService.updateOrderStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "Order deleted successfully";
    }
}