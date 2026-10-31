
package com.shopsphere.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.entity.Payment;
import com.shopsphere.repository.CustomerOrderRepository;
import com.shopsphere.repository.PaymentRepository;
import com.shopsphere.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Set<String> VALID_STATUSES = Set.of(
            "PENDING", "SUCCESS", "FAILED", "REFUNDED"
    );

    private final PaymentRepository paymentRepository;
    private final CustomerOrderRepository orderRepository;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            CustomerOrderRepository orderRepository) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Payment createPayment(Long orderId, Payment payment) {

        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (paymentRepository.existsByOrder_Id(orderId)) {
            throw new RuntimeException("Payment already exists for this order");
        }

        if (payment.getAmount() == null
                || payment.getAmount() <= 0) {
            throw new RuntimeException("Payment amount must be positive");
        }

        if (payment.getAmount() > order.getTotalAmount()) {
            throw new RuntimeException("Payment amount cannot exceed order total");
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
                .orElseThrow(() -> new RuntimeException("Payment not found for this order"));
    }

    @Override
    public Payment updatePaymentStatus(Long id, String status) {

        Payment payment = getPaymentById(id);

        if (status == null || !VALID_STATUSES.contains(status.toUpperCase())) {
            throw new RuntimeException("Invalid payment status. Valid statuses: "
                    + VALID_STATUSES);
        }

        payment.setPaymentStatus(status.toUpperCase());

        return paymentRepository.save(payment);
    }

    @Override
    public void deletePayment(Long id) {
        Payment payment = getPaymentById(id);
        paymentRepository.delete(payment);
    }
}
