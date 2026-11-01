
package com.shopsphere.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopsphere.entity.OrderStatusHistory;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.CustomerOrderRepository;
import com.shopsphere.repository.OrderStatusHistoryRepository;
import com.shopsphere.service.OrderStatusHistoryService;

@Service
public class OrderStatusHistoryServiceImpl
        implements OrderStatusHistoryService {

    private final OrderStatusHistoryRepository historyRepository;
    private final CustomerOrderRepository orderRepository;

    public OrderStatusHistoryServiceImpl(
            OrderStatusHistoryRepository historyRepository,
            CustomerOrderRepository orderRepository) {

        this.historyRepository = historyRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderStatusHistory> getOrderStatusHistory(Long orderId) {

        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException(
                    "Order not found with ID: " + orderId);
        }

        return historyRepository
                .findByOrder_IdOrderByStatusChangedAtDesc(orderId);
    }
}
