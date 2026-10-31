package com.shopsphere.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.OrderStatusHistory;
import com.shopsphere.service.OrderStatusHistoryService;

@RestController
@RequestMapping("/api/orders")
public class OrderStatusHistoryController {

    private final OrderStatusHistoryService historyService;

    public OrderStatusHistoryController(
            OrderStatusHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/{orderId}/status-history")
    public List<OrderStatusHistory> getOrderStatusHistory(
            @PathVariable Long orderId) {

        return historyService.getOrderStatusHistory(orderId);
    }
}