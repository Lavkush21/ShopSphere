
package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.Shipment;

public interface ShipmentService {

    Shipment createShipment(Long orderId, String trackingNumber,
                            String courierName);

    List<Shipment> getAllShipments();

    Shipment getShipmentById(Long id);

    Shipment getShipmentByOrderId(Long orderId);

    Shipment getShipmentByTrackingNumber(String trackingNumber);

    Shipment updateShipmentStatus(Long id, String status);

    void deleteShipment(Long id);
}
