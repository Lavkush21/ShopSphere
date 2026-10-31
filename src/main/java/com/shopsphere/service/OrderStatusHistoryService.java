package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.OrderStatusHistory;

public interface OrderStatusHistoryService {

    List<OrderStatusHistory> getOrderStatusHistory(Long orderId);
}