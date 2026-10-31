package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.WishlistItem;

public interface WishlistService {

    WishlistItem addToWishlist(Long customerId, Long productId);

    List<WishlistItem> getWishlist(Long customerId);

    void removeFromWishlist(Long customerId, Long productId);

    void clearWishlist(Long customerId);
}