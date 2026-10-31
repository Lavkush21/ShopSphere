
package com.shopsphere.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.Shipment;
import com.shopsphere.service.ShipmentService;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping("/{orderId}")
    public Shipment createShipment(
            @PathVariable Long orderId,
            @RequestParam String trackingNumber,
            @RequestParam String courierName) {

        return shipmentService.createShipment(
                orderId, trackingNumber, courierName);
    }

    @GetMapping
    public List<Shipment> getAllShipments() {
        return shipmentService.getAllShipments();
    }

    @GetMapping("/{id}")
    public Shipment getShipmentById(@PathVariable Long id) {
        return shipmentService.getShipmentById(id);
    }

    @GetMapping("/order/{orderId}")
    public Shipment getShipmentByOrderId(@PathVariable Long orderId) {
        return shipmentService.getShipmentByOrderId(orderId);
    }

    @GetMapping("/tracking/{trackingNumber}")
    public Shipment getShipmentByTrackingNumber(
            @PathVariable String trackingNumber) {

        return shipmentService.getShipmentByTrackingNumber(trackingNumber);
    }

    @PutMapping("/{id}/status")
    public Shipment updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return shipmentService.updateShipmentStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public String deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return "Shipment deleted successfully";
    }
}
