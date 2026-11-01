package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.Payment;

public interface PaymentService {

    Payment createPayment(Long orderId, Payment payment);

    List<Payment> getAllPayments();

    Payment getPaymentById(Long id);

    Payment getPaymentByOrderId(Long orderId);

    List<Payment> getPaymentHistoryByCustomerId(Long customerId);

    Payment updatePaymentStatus(Long id, String status);

    Payment refundPayment(Long id);

    // Refund payment after an approved return
    Payment refundForReturn(Long paymentId);

    void deletePayment(Long id);
}