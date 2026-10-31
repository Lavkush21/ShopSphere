package com.shopsphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.entity.CustomerOrder;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    boolean existsByOrderNumber(String orderNumber);

    List<CustomerOrder> findByCustomer_Id(Long customerId);
}