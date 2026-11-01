
package com.shopsphere.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.entity.Payment;
import com.shopsphere.repository.CustomerOrderRepository;
import com.shopsphere.repository.PaymentRepository;
import com.shopsphere.service.CustomerOrderService;
import com.shopsphere.service.OrderEmailNotificationService;
import com.shopsphere.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Set<String> VALID_STATUSES = Set.of(
            "PENDING", "SUCCESS", "FAILED", "REFUNDED"
    );

    private final PaymentRepository paymentRepository;
    private final CustomerOrderRepository orderRepository;
    private final CustomerOrderService customerOrderService;
    private final OrderEmailNotificationService orderEmailNotificationService;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            CustomerOrderRepository orderRepository,
            CustomerOrderService customerOrderService,
            OrderEmailNotificationService orderEmailNotificationService) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.customerOrderService = customerOrderService;
        this.orderEmailNotificationService = orderEmailNotificationService;
    }

    @Override
    public Payment createPayment(Long orderId, Payment payment) {

        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (paymentRepository.existsByOrder_Id(orderId)) {
            throw new RuntimeException(
                    "Payment already exists for this order");
        }

        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new RuntimeException("Payment amount must be positive");
        }

        if (payment.getAmount() > order.getTotalAmount()) {
            throw new RuntimeException(
                    "Payment amount cannot exceed order total");
        }

        payment.setOrder(order);
        payment.setPaymentStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Override
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found for this order"));
    }

    @Override
    public List<Payment> getPaymentHistoryByCustomerId(Long customerId) {
        return paymentRepository.findByOrder_Customer_Id(customerId);
    }

    @Override
    @Transactional
    public Payment updatePaymentStatus(Long id, String status) {

        Payment payment = getPaymentById(id);

        if (status == null
                || !VALID_STATUSES.contains(status.toUpperCase())) {
            throw new RuntimeException("Invalid payment status");
        }

        String newStatus = status.toUpperCase();
        String currentStatus = payment.getPaymentStatus();

        CustomerOrder order = payment.getOrder();

        if ("SUCCESS".equals(newStatus)) {

            if (!"PENDING".equals(currentStatus)) {
                throw new RuntimeException(
                        "Only pending payments can be marked as SUCCESS");
            }

            if ("CANCELLED".equals(order.getStatus())) {
                throw new RuntimeException(
                        "Cannot pay for a cancelled order");
            }

            if (!payment.getAmount().equals(order.getTotalAmount())) {
                throw new RuntimeException(
                        "Payment amount must match order total");
            }

            order.setStatus("CONFIRMED");
            orderRepository.save(order);
        }

        else if ("FAILED".equals(newStatus)) {

            if (!"PENDING".equals(currentStatus)) {
                throw new RuntimeException(
                        "Only pending payments can be marked as FAILED");
            }
        }

        else if ("REFUNDED".equals(newStatus)) {

            if (!"SUCCESS".equals(currentStatus)) {
                throw new RuntimeException(
                        "Only successful payments can be refunded");
            }

            // Cancel order and restore product stock
            customerOrderService.cancelOrder(order.getId());
        }

        else if ("PENDING".equals(newStatus)) {
            throw new RuntimeException(
                    "Payment cannot be changed back to PENDING");
        }

        payment.setPaymentStatus(newStatus);

        Payment savedPayment = paymentRepository.save(payment);

        // Send Payment Success Email
        if ("SUCCESS".equals(newStatus)) {
            try {
                orderEmailNotificationService.sendPaymentSuccess(
                        order.getCustomer().getEmail(),
                        order.getId(),
                        payment.getAmount()
                );
            } catch (Exception e) {
                System.err.println(
                        "Payment success email failed: " + e.getMessage());
            }
        }

        return savedPayment;
    }

    @Override
    @Transactional
    public Payment refundPayment(Long id) {
        return updatePaymentStatus(id, "REFUNDED");
    }

    @Override
    public void deletePayment(Long id) {
        Payment payment = getPaymentById(id);
        paymentRepository.delete(payment);
    }
}
