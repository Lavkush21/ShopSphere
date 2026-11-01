package com.shopsphere.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.entity.Payment;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.CustomerOrderRepository;
import com.shopsphere.repository.PaymentRepository;
import com.shopsphere.service.CustomerOrderService;
import com.shopsphere.service.OrderEmailNotificationService;
import com.shopsphere.service.PaymentService;

@Service
@Transactional
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + orderId));

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

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException(
                    "Payment can only be created for pending orders");
        }

        payment.setOrder(order);
        payment.setPaymentStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for Order ID: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentHistoryByCustomerId(Long customerId) {
        return paymentRepository.findByOrder_Customer_Id(customerId);
    }

    @Override
    public Payment updatePaymentStatus(Long id, String status) {

        Payment payment = getPaymentById(id);

        if (status == null || status.isBlank()) {
            throw new RuntimeException("Payment status is required");
        }

        String newStatus = status.trim().toUpperCase();
        String currentStatus = payment.getPaymentStatus();

        if (!VALID_STATUSES.contains(newStatus)) {
            throw new RuntimeException("Invalid payment status");
        }

        CustomerOrder order = payment.getOrder();

        if ("SUCCESS".equals(newStatus)) {

            if (!"PENDING".equals(currentStatus)) {
                throw new RuntimeException(
                        "Only pending payments can be marked as SUCCESS");
            }

            if (!"PENDING".equals(order.getStatus())) {
                throw new RuntimeException(
                        "Only pending orders can be confirmed through payment");
            }

            if (!payment.getAmount().equals(order.getTotalAmount())) {
                throw new RuntimeException(
                        "Payment amount must match order total");
            }

            customerOrderService.updateOrderStatus(
                    order.getId(), "CONFIRMED");
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

            customerOrderService.cancelOrder(order.getId());
        }

        else if ("PENDING".equals(newStatus)) {

            throw new RuntimeException(
                    "Payment cannot be changed back to PENDING");
        }

        payment.setPaymentStatus(newStatus);

        Payment savedPayment = paymentRepository.save(payment);

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
    public Payment refundPayment(Long id) {
        return updatePaymentStatus(id, "REFUNDED");
    }

    @Override
    public Payment refundForReturn(Long paymentId) {

        Payment payment = getPaymentById(paymentId);

        if (!"SUCCESS".equals(payment.getPaymentStatus())) {
            throw new RuntimeException(
                    "Only successful payments can be refunded for a return");
        }

        CustomerOrder order = payment.getOrder();

        if (!"DELIVERED".equals(order.getStatus())) {
            throw new RuntimeException(
                    "Only delivered orders can be returned");
        }

        customerOrderService.completeReturn(order.getId());

        payment.setPaymentStatus("REFUNDED");

        return paymentRepository.save(payment);
    }

    @Override
    public void deletePayment(Long id) {
        Payment payment = getPaymentById(id);
        paymentRepository.delete(payment);
    }
}