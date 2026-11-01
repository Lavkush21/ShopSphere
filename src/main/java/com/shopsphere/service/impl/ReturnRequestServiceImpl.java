
package com.shopsphere.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.entity.Payment;
import com.shopsphere.entity.ReturnRequest;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.CustomerOrderRepository;
import com.shopsphere.repository.ReturnRequestRepository;
import com.shopsphere.service.PaymentService;
import com.shopsphere.service.ReturnRequestService;

@Service
@Transactional
public class ReturnRequestServiceImpl implements ReturnRequestService {

    private final ReturnRequestRepository returnRequestRepository;
    private final CustomerOrderRepository orderRepository;
    private final PaymentService paymentService;

    public ReturnRequestServiceImpl(
            ReturnRequestRepository returnRequestRepository,
            CustomerOrderRepository orderRepository,
            PaymentService paymentService) {

        this.returnRequestRepository = returnRequestRepository;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }

    @Override
    public ReturnRequest createReturnRequest(Long orderId, String reason) {

        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + orderId));

        if (!"DELIVERED".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException(
                    "Return request is allowed only for delivered orders");
        }

        if (reason == null || reason.isBlank()) {
            throw new RuntimeException("Return reason is required");
        }

        if (returnRequestRepository.existsByOrder_Id(orderId)) {
            throw new RuntimeException(
                    "Return request already exists for this order");
        }

        ReturnRequest request = new ReturnRequest();
        request.setOrder(order);
        request.setReason(reason.trim());
        request.setStatus("REQUESTED");

        return returnRequestRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequest> getAllReturnRequests() {
        return returnRequestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnRequest getReturnRequestById(Long id) {
        return returnRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Return request not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequest> getReturnRequestsByOrderId(Long orderId) {

        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException(
                    "Order not found with ID: " + orderId);
        }

        return returnRequestRepository.findByOrder_Id(orderId);
    }

    @Override
    public ReturnRequest updateReturnRequestStatus(Long id, String status) {

        ReturnRequest request = getReturnRequestById(id);

        if (status == null || status.isBlank()) {
            throw new RuntimeException("Return status is required");
        }

        String newStatus = status.trim().toUpperCase();

        Set<String> allowedStatuses = Set.of(
                "APPROVED", "REJECTED", "COMPLETED");

        if (!allowedStatuses.contains(newStatus)) {
            throw new RuntimeException("Invalid return status");
        }

        if (!"REQUESTED".equals(request.getStatus())
                && !("APPROVED".equals(request.getStatus())
                && "COMPLETED".equals(newStatus))) {
            throw new RuntimeException(
                    "Return request status cannot be changed");
        }

        if ("APPROVED".equals(newStatus)) {
            request.setApprovedDate(LocalDateTime.now());
        }

        if ("COMPLETED".equals(newStatus)) {

            CustomerOrder order = request.getOrder();

            Payment payment = paymentService.getPaymentByOrderId(
                    order.getId());

            paymentService.refundForReturn(payment.getId());

            request.setCompletedDate(LocalDateTime.now());
        }

        request.setStatus(newStatus);

        return returnRequestRepository.save(request);
    }

    @Override
    public void deleteReturnRequest(Long id) {

        ReturnRequest request = getReturnRequestById(id);
        returnRequestRepository.delete(request);
    }
}
