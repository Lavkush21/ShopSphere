package com.shopsphere.service;

import org.springframework.stereotype.Service;

@Service
public class OrderEmailNotificationService {

    private final EmailService emailService;

    public OrderEmailNotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendOrderConfirmation(String email, Long orderId, Double amount) {
        String subject = "ShopSphere - Order Confirmation";

        String body = "Dear Customer,\n\n"
                + "Your order has been placed successfully!\n"
                + "Order ID: " + orderId + "\n"
                + "Total Amount: Rs. " + amount + "\n\n"
                + "Thank you for shopping with ShopSphere.";

        emailService.sendEmail(email, subject, body);
    }

    public void sendPaymentSuccess(String email, Long orderId, Double amount) {
        String subject = "ShopSphere - Payment Successful";

        String body = "Dear Customer,\n\n"
                + "Your payment was successful.\n"
                + "Order ID: " + orderId + "\n"
                + "Paid Amount: Rs. " + amount + "\n\n"
                + "Thank you for shopping with ShopSphere.";

        emailService.sendEmail(email, subject, body);
    }

    public void sendShipmentNotification(String email, Long orderId, String trackingNumber) {
        String subject = "ShopSphere - Order Shipped";

        String body = "Dear Customer,\n\n"
                + "Your order has been shipped!\n"
                + "Order ID: " + orderId + "\n"
                + "Tracking Number: " + trackingNumber + "\n\n"
                + "Thank you for shopping with ShopSphere.";

        emailService.sendEmail(email, subject, body);
    }

    public void sendDeliveryConfirmation(String email, Long orderId) {
        String subject = "ShopSphere - Order Delivered";

        String body = "Dear Customer,\n\n"
                + "Your order has been delivered successfully.\n"
                + "Order ID: " + orderId + "\n\n"
                + "Thank you for shopping with ShopSphere.";

        emailService.sendEmail(email, subject, body);
    }
}