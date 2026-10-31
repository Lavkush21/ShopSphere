
package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.Notification;

public interface NotificationService {

    Notification createNotification(
            Long customerId,
            String message,
            String type);

    List<Notification> getAllNotifications();

    List<Notification> getNotificationsByCustomerId(Long customerId);

    List<Notification> getUnreadNotifications(Long customerId);

    Notification markAsRead(Long notificationId);

    void deleteNotification(Long notificationId);
}
