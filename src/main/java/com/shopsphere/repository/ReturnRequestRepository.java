package com.shopsphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.ReturnRequest;

public interface ReturnRequestRepository
        extends JpaRepository<ReturnRequest, Long> {

    List<ReturnRequest> findByOrder_Id(Long orderId);

    boolean existsByOrder_Id(Long orderId);
}