package com.shopsphere.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopsphere.entity.OrderStatusHistory;
import com.shopsphere.repository.OrderStatusHistoryRepository;
import com.shopsphere.service.OrderStatusHistoryService;

@Service
public class OrderStatusHistoryServiceImpl
        implements OrderStatusHistoryService {

    private final OrderStatusHistoryRepository historyRepository;

    public OrderStatusHistoryServiceImpl(
            OrderStatusHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    public List<OrderStatusHistory> getOrderStatusHistory(Long orderId) {
        return historyRepository
                .findByOrder_IdOrderByStatusChangedAtDesc(orderId);
    }
}