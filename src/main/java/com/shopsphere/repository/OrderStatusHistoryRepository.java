package com.shopsphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.OrderStatusHistory;

public interface OrderStatusHistoryRepository
        extends JpaRepository<OrderStatusHistory, Long> {

    List<OrderStatusHistory> findByOrder_IdOrderByStatusChangedAtDesc(Long orderId);
}