package com.shopsphere.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.entity.Shipment;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.CustomerOrderRepository;
import com.shopsphere.repository.ShipmentRepository;
import com.shopsphere.service.CustomerOrderService;
import com.shopsphere.service.ShipmentService;
import com.shopsphere.service.OrderEmailNotificationService;

@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final CustomerOrderRepository orderRepository;
    private final CustomerOrderService customerOrderService;
    private final OrderEmailNotificationService orderEmailNotificationService;

    public ShipmentServiceImpl(
            ShipmentRepository shipmentRepository,
            CustomerOrderRepository orderRepository,
            CustomerOrderService customerOrderService,
            OrderEmailNotificationService orderEmailNotificationService) {

        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.customerOrderService = customerOrderService;
        this.orderEmailNotificationService = orderEmailNotificationService;
    }

    @Override
    public Shipment createShipment(Long orderId, String trackingNumber,
                                   String courierName) {

        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + orderId));

        if (shipmentRepository.existsByOrder_Id(orderId)) {
            throw new RuntimeException(
                    "Shipment already exists for this order");
        }

        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new RuntimeException("Tracking number is required");
        }

        if (courierName == null || courierName.isBlank()) {
            throw new RuntimeException("Courier name is required");
        }

        if (shipmentRepository.findByTrackingNumber(trackingNumber).isPresent()) {
            throw new RuntimeException("Tracking number already exists");
        }

        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new RuntimeException(
                    "Only confirmed orders can be shipped");
        }

        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setTrackingNumber(trackingNumber);
        shipment.setCourierName(courierName);
        shipment.setStatus("CREATED");

        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Shipment getShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipment not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Shipment getShipmentByOrderId(Long orderId) {
        return shipmentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipment not found for Order ID: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public Shipment getShipmentByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipment not found with tracking number: "
                                + trackingNumber));
    }

    @Override
    public Shipment updateShipmentStatus(Long id, String status) {

        Shipment shipment = getShipmentById(id);

        if (status == null || status.isBlank()) {
            throw new RuntimeException("Shipment status is required");
        }

        String newStatus = status.trim().toUpperCase();
        String currentStatus = shipment.getStatus();

        if (!List.of("CREATED", "SHIPPED", "IN_TRANSIT",
                "OUT_FOR_DELIVERY", "DELIVERED", "RETURNED")
                .contains(newStatus)) {

            throw new RuntimeException("Invalid shipment status");
        }

        if ("DELIVERED".equals(currentStatus)
                || "RETURNED".equals(currentStatus)) {

            throw new RuntimeException("Shipment is already completed");
        }

        CustomerOrder order = shipment.getOrder();

        if ("SHIPPED".equals(newStatus)
                && !"SHIPPED".equals(currentStatus)) {

            customerOrderService.updateOrderStatus(
                    order.getId(), "SHIPPED");

            if (shipment.getShippedAt() == null) {
                shipment.setShippedAt(LocalDateTime.now());

                try {
                    orderEmailNotificationService.sendShipmentNotification(
                            order.getCustomer().getEmail(),
                            order.getId(),
                            shipment.getTrackingNumber()
                    );
                } catch (Exception e) {
                    System.err.println(
                            "Shipment notification email failed: "
                                    + e.getMessage());
                }
            }
        }

        if ("DELIVERED".equals(newStatus)) {

            customerOrderService.updateOrderStatus(
                    order.getId(), "DELIVERED");

            shipment.setDeliveredAt(LocalDateTime.now());

            try {
                orderEmailNotificationService.sendDeliveryConfirmation(
                        order.getCustomer().getEmail(),
                        order.getId()
                );
            } catch (Exception e) {
                System.err.println(
                        "Delivery confirmation email failed: "
                                + e.getMessage());
            }
        }

        shipment.setStatus(newStatus);

        return shipmentRepository.save(shipment);
    }

    @Override
    public void deleteShipment(Long id) {

        Shipment shipment = getShipmentById(id);
        shipmentRepository.delete(shipment);
    }
}