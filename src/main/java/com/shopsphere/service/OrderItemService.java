package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.OrderItem;

public interface OrderItemService {

    OrderItem addOrderItem(Long orderId, Long productId, Integer quantity);

    List<OrderItem> getItemsByOrderId(Long orderId);

    void deleteOrderItem(Long itemId);
}