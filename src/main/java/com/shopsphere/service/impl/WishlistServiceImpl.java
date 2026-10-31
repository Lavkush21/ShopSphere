package com.shopsphere.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.*;
import com.shopsphere.repository.*;
import com.shopsphere.service.WishlistService;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public WishlistServiceImpl(
            WishlistRepository wishlistRepository,
            WishlistItemRepository wishlistItemRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository) {

        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public WishlistItem addToWishlist(Long customerId, Long productId) {

        customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setCustomerId(customerId);
                    return wishlistRepository.save(newWishlist);
                });

        if (wishlistItemRepository
                .findByWishlist_IdAndProduct_Id(wishlist.getId(), productId)
                .isPresent()) {
            throw new RuntimeException("Product already exists in wishlist");
        }

        WishlistItem item = new WishlistItem();
        item.setWishlist(wishlist);
        item.setProduct(product);

        return wishlistItemRepository.save(item);
    }

    @Override
    public List<WishlistItem> getWishlist(Long customerId) {

        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found"));

        return wishlistItemRepository.findByWishlist_Id(wishlist.getId());
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long customerId, Long productId) {

        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found"));

        WishlistItem item = wishlistItemRepository
                .findByWishlist_IdAndProduct_Id(wishlist.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));

        wishlistItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void clearWishlist(Long customerId) {

        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found"));

        wishlistItemRepository.deleteByWishlist_Id(wishlist.getId());
    }
}