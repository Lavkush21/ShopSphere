
package com.shopsphere.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.entity.Shipment;
import com.shopsphere.repository.CustomerOrderRepository;
import com.shopsphere.repository.ShipmentRepository;
import com.shopsphere.service.ShipmentService;
import com.shopsphere.service.OrderEmailNotificationService;

@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final CustomerOrderRepository orderRepository;
    private final OrderEmailNotificationService orderEmailNotificationService;

    public ShipmentServiceImpl(
            ShipmentRepository shipmentRepository,
            CustomerOrderRepository orderRepository,
            OrderEmailNotificationService orderEmailNotificationService) {

        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.orderEmailNotificationService = orderEmailNotificationService;
    }

    @Override
    public Shipment createShipment(Long orderId, String trackingNumber,
                                   String courierName) {

        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

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

        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Cannot ship a cancelled order");
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
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Shipment getShipmentByOrderId(Long orderId) {
        return shipmentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Shipment getShipmentByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
    }

    @Override
    public Shipment updateShipmentStatus(Long id, String status) {

        Shipment shipment = getShipmentById(id);

        if (status == null || status.isBlank()) {
            throw new RuntimeException("Shipment status is required");
        }

        String newStatus = status.trim().toUpperCase();

        if (!List.of("CREATED", "SHIPPED", "IN_TRANSIT",
                "OUT_FOR_DELIVERY", "DELIVERED", "RETURNED")
                .contains(newStatus)) {

            throw new RuntimeException("Invalid shipment status");
        }

        if ("DELIVERED".equals(shipment.getStatus())
                || "RETURNED".equals(shipment.getStatus())) {

            throw new RuntimeException("Shipment is already completed");
        }

        shipment.setStatus(newStatus);

        // Shipment notification email
        if ("SHIPPED".equals(newStatus) && shipment.getShippedAt() == null) {

            shipment.setShippedAt(LocalDateTime.now());

            try {
                CustomerOrder order = shipment.getOrder();

                orderEmailNotificationService.sendShipmentNotification(
                        order.getCustomer().getEmail(),
                        order.getId(),
                        shipment.getTrackingNumber()
                );

            } catch (Exception e) {
                System.err.println(
                        "Shipment notification email failed: " + e.getMessage());
            }
        }

        // Delivery confirmation email
        if ("DELIVERED".equals(newStatus)) {

            shipment.setDeliveredAt(LocalDateTime.now());

            try {
                CustomerOrder order = shipment.getOrder();

                orderEmailNotificationService.sendDeliveryConfirmation(
                        order.getCustomer().getEmail(),
                        order.getId()
                );

            } catch (Exception e) {
                System.err.println(
                        "Delivery confirmation email failed: " + e.getMessage());
            }
        }

        return shipmentRepository.save(shipment);
    }

    @Override
    public void deleteShipment(Long id) {

        Shipment shipment = getShipmentById(id);
        shipmentRepository.delete(shipment);
    }
}
