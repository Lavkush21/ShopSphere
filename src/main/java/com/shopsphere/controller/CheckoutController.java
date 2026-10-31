package com.shopsphere.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.service.CheckoutService;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/{customerId}")
    public ResponseEntity<CustomerOrder> placeOrder(
            @PathVariable Long customerId) {

        CustomerOrder order = checkoutService.placeOrder(customerId);

        return ResponseEntity.status(201).body(order);
    }
}