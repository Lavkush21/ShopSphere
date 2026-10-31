package com.shopsphere.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.OrderItem;
import com.shopsphere.service.OrderItemService;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public OrderItem addOrderItem(
            @RequestParam Long orderId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        return orderItemService.addOrderItem(orderId, productId, quantity);
    }

    @GetMapping("/order/{orderId}")
    public List<OrderItem> getItemsByOrderId(
            @PathVariable Long orderId) {

        return orderItemService.getItemsByOrderId(orderId);
    }

    @DeleteMapping("/{itemId}")
    public String deleteOrderItem(@PathVariable Long itemId) {

        orderItemService.deleteOrderItem(itemId);
        return "Order item deleted successfully";
    }
}