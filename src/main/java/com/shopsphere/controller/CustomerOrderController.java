
package com.shopsphere.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.service.CustomerOrderService;

@RestController
@RequestMapping("/api/orders")
public class CustomerOrderController {

    private final CustomerOrderService orderService;

    public CustomerOrderController(CustomerOrderService orderService) {
        this.orderService = orderService;
    }

    // Create Order
    @PostMapping
    public CustomerOrder createOrder(
            @RequestBody CustomerOrder order,
            @RequestParam Long customerId) {

        return orderService.createOrder(order, customerId);
    }

    // Get All Orders
    @GetMapping
    public List<CustomerOrder> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Get Order By ID
    @GetMapping("/{id}")
    public CustomerOrder getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    // Get Orders By Customer ID
    @GetMapping("/customer/{customerId}")
    public List<CustomerOrder> getOrdersByCustomerId(
            @PathVariable Long customerId) {

        return orderService.getOrdersByCustomerId(customerId);
    }

    // Update Order
    @PutMapping("/{id}")
    public CustomerOrder updateOrder(
            @PathVariable Long id,
            @RequestBody CustomerOrder order) {

        return orderService.updateOrder(id, order);
    }

    // Update Order Status
    @PutMapping("/{id}/status")
    public CustomerOrder updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return orderService.updateOrderStatus(id, status);
    }

    // Cancel Order and Restore Product Stock
    @PutMapping("/{id}/cancel")
    public CustomerOrder cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    // Delete Order
    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {

        orderService.deleteOrder(id);

        return "Order deleted successfully";
    }
}
