package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.Payment;

public interface PaymentService {

    Payment createPayment(Long orderId, Payment payment);

    List<Payment> getAllPayments();

    Payment getPaymentById(Long id);

    Payment getPaymentByOrderId(Long orderId);

    Payment updatePaymentStatus(Long id, String status);

    void deletePayment(Long id);
}