
package com.shopsphere.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tracking number is required")
    @Column(unique = true, nullable = false)
    private String trackingNumber;

    @NotBlank(message = "Courier name is required")
    private String courierName;

    private String status = "CREATED";

    private LocalDateTime createdAt;

    private LocalDateTime shippedAt;

    private LocalDateTime estimatedDeliveryDate;

    private LocalDateTime deliveredAt;

    @NotNull(message = "Order is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private CustomerOrder order;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public CustomerOrder getOrder() {
        return order;
    }

    public void setOrder(CustomerOrder order) {
        this.order = order;
    }
}
