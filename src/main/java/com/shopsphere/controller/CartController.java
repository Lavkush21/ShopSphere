
package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            @RequestParam Long customerId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                cartService.addToCart(customerId, productId, quantity));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<CartItem>> getCartItems(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                cartService.getCartItems(customerId));
    }

    @PutMapping("/update")
    public ResponseEntity<Cart> updateCartItem(
            @RequestParam Long customerId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                cartService.updateCartItem(customerId, productId, quantity));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeCartItem(
            @RequestParam Long customerId,
            @RequestParam Long productId) {

        cartService.removeCartItem(customerId, productId);

        return ResponseEntity.ok("Cart item removed successfully");
    }

    @DeleteMapping("/clear/{customerId}")
    public ResponseEntity<String> clearCart(
            @PathVariable Long customerId) {

        cartService.clearCart(customerId);

        return ResponseEntity.ok("Cart cleared successfully");
    }
}
