
package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find payment by order ID
    Optional<Payment> findByOrder_Id(Long orderId);

    // Check whether payment exists for an order
    boolean existsByOrder_Id(Long orderId);

    // Get payment history by customer ID
    List<Payment> findByOrder_Customer_Id(Long customerId);
}
