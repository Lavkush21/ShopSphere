
package com.shopsphere.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.Customer;
import com.shopsphere.entity.Notification;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.CustomerRepository;
import com.shopsphere.repository.NotificationRepository;
import com.shopsphere.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            CustomerRepository customerRepository) {

        this.notificationRepository = notificationRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Notification createNotification(
            Long customerId, String message, String type) {

        if (message == null || message.isBlank()) {
            throw new RuntimeException("Notification message is required");
        }

        if (type == null || type.isBlank()) {
            throw new RuntimeException("Notification type is required");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + customerId));

        Notification notification = new Notification();
        notification.setCustomer(customer);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getNotificationsByCustomerId(Long customerId) {

        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + customerId));

        return notificationRepository
                .findByCustomer_IdOrderByCreatedAtDesc(customerId);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long customerId) {

        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + customerId));

        return notificationRepository
                .findByCustomer_IdAndReadFalseOrderByCreatedAtDesc(customerId);
    }

    @Override
    @Transactional
    public Notification markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with ID: " + notificationId));

        notification.setRead(true);

        return notificationRepository.save(notification);
    }

    @Override
    public void deleteNotification(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with ID: " + notificationId));

        notificationRepository.delete(notification);
    }
}
