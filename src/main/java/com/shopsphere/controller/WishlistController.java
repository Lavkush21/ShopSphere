package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.WishlistItem;
import com.shopsphere.service.WishlistService;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/add")
    public ResponseEntity<WishlistItem> addToWishlist(
            @RequestParam Long customerId,
            @RequestParam Long productId) {

        return new ResponseEntity<>(
                wishlistService.addToWishlist(customerId, productId),
                HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<WishlistItem>> getWishlist(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                wishlistService.getWishlist(customerId));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromWishlist(
            @RequestParam Long customerId,
            @RequestParam Long productId) {

        wishlistService.removeFromWishlist(customerId, productId);
        return ResponseEntity.ok("Product removed from wishlist");
    }

    @DeleteMapping("/clear/{customerId}")
    public ResponseEntity<String> clearWishlist(
            @PathVariable Long customerId) {

        wishlistService.clearWishlist(customerId);
        return ResponseEntity.ok("Wishlist cleared successfully");
    }
}