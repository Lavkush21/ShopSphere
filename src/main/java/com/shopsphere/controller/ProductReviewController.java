package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.ProductReview;
import com.shopsphere.service.ProductReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class ProductReviewController {

    private final ProductReviewService reviewService;

    public ProductReviewController(ProductReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ProductReview> addReview(
            @RequestParam Long customerId,
            @RequestParam Long productId,
            @Valid @RequestBody ProductReview review) {

        return new ResponseEntity<>(
                reviewService.addReview(customerId, productId, review),
                HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductReview>> getReviewsByProduct(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                reviewService.getReviewsByProductId(productId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ProductReview>> getReviewsByCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                reviewService.getReviewsByCustomerId(customerId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ProductReview> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ProductReview review) {

        return ResponseEntity.ok(
                reviewService.updateReview(reviewId, review));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long reviewId) {

        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("Review deleted successfully");
    }
}