
package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;

public interface CartService {

    Cart addToCart(Long customerId, Long productId, Integer quantity);

    List<CartItem> getCartItems(Long customerId);

    Cart updateCartItem(Long customerId, Long productId, Integer quantity);

    void removeCartItem(Long customerId, Long productId);

    void clearCart(Long customerId);
}
