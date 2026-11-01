package com.shopsphere.controller;

import org.springframework.web.bind.annotation.*;

import com.shopsphere.service.EmailService;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public String sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body) {

        emailService.sendEmail(to, subject, body);

        return "Email sent successfully!";
    }
}