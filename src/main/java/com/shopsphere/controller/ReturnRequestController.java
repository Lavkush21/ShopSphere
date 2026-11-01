package com.shopsphere.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.ReturnRequest;
import com.shopsphere.service.ReturnRequestService;

@RestController
@RequestMapping("/api/returns")
public class ReturnRequestController {

    private final ReturnRequestService returnRequestService;

    public ReturnRequestController(ReturnRequestService returnRequestService) {
        this.returnRequestService = returnRequestService;
    }

    // Create return request
    @PostMapping("/{orderId}")
    public ReturnRequest createReturnRequest(
            @PathVariable Long orderId,
            @RequestParam String reason) {

        return returnRequestService.createReturnRequest(orderId, reason);
    }

    // Get all return requests
    @GetMapping
    public List<ReturnRequest> getAllReturnRequests() {
        return returnRequestService.getAllReturnRequests();
    }

    // Get return request by ID
    @GetMapping("/{id}")
    public ReturnRequest getReturnRequestById(@PathVariable Long id) {
        return returnRequestService.getReturnRequestById(id);
    }

    // Get return requests by order ID
    @GetMapping("/order/{orderId}")
    public List<ReturnRequest> getReturnRequestsByOrderId(
            @PathVariable Long orderId) {

        return returnRequestService.getReturnRequestsByOrderId(orderId);
    }

    // Approve or reject or complete return request
    @PutMapping("/{id}/status")
    public ReturnRequest updateReturnRequestStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return returnRequestService.updateReturnRequestStatus(id, status);
    }

    // Delete return request
    @DeleteMapping("/{id}")
    public String deleteReturnRequest(@PathVariable Long id) {
        returnRequestService.deleteReturnRequest(id);
        return "Return request deleted successfully";
    
    }
}