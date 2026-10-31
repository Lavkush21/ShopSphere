
package com.shopsphere.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.Notification;
import com.shopsphere.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Create Notification
    @PostMapping("/{customerId}")
    public Notification createNotification(
            @PathVariable Long customerId,
            @RequestParam String message,
            @RequestParam String type) {

        return notificationService.createNotification(
                customerId, message, type);
    }

    // Get All Notifications
    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    // Get Customer Notifications
    @GetMapping("/customer/{customerId}")
    public List<Notification> getNotificationsByCustomerId(
            @PathVariable Long customerId) {

        return notificationService
                .getNotificationsByCustomerId(customerId);
    }

    // Get Unread Notifications
    @GetMapping("/customer/{customerId}/unread")
    public List<Notification> getUnreadNotifications(
            @PathVariable Long customerId) {

        return notificationService.getUnreadNotifications(customerId);
    }

    // Mark Notification As Read
    @PutMapping("/{notificationId}/read")
    public Notification markAsRead(
            @PathVariable Long notificationId) {

        return notificationService.markAsRead(notificationId);
    }

    // Delete Notification
    @DeleteMapping("/{notificationId}")
    public String deleteNotification(
            @PathVariable Long notificationId) {

        notificationService.deleteNotification(notificationId);
        return "Notification deleted successfully";
    }
}
