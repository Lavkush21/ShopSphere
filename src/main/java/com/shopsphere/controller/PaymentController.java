
package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.Payment;
import com.shopsphere.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Create Payment
    @PostMapping
    public ResponseEntity<Payment> createPayment(
            @RequestParam Long orderId,
            @Valid @RequestBody Payment payment) {

        return new ResponseEntity<>(
                paymentService.createPayment(orderId, payment),
                HttpStatus.CREATED);
    }

    // Get All Payments
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {

        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // Get Payment By ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.getPaymentById(id));
    }

    // Get Payment By Order ID
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                paymentService.getPaymentByOrderId(orderId));
    }

    // Get Payment History By Customer ID
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Payment>> getPaymentHistoryByCustomerId(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                paymentService.getPaymentHistoryByCustomerId(customerId));
    }

    // Update Payment Status
    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                paymentService.updatePaymentStatus(id, status));
    }

    // Refund Payment
    @PutMapping("/{id}/refund")
    public ResponseEntity<Payment> refundPayment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.refundPayment(id));
    }

    // Delete Payment
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePayment(
            @PathVariable Long id) {

        paymentService.deletePayment(id);

        return ResponseEntity.ok(
                "Payment deleted successfully");
    }
}
