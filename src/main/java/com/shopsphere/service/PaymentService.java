
package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.Payment;

public interface PaymentService {

    // Create payment
    Payment createPayment(Long orderId, Payment payment);

    // Get all payments
    List<Payment> getAllPayments();

    // Get payment by ID
    Payment getPaymentById(Long id);

    // Get payment by order ID
    Payment getPaymentByOrderId(Long orderId);

    // Get payment history by customer ID
    List<Payment> getPaymentHistoryByCustomerId(Long customerId);

    // Update payment status
    Payment updatePaymentStatus(Long id, String status);

    // Refund payment
    Payment refundPayment(Long id);

    // Delete payment
    void deletePayment(Long id);
}
