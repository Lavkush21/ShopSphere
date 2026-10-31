package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.entity.WishlistItem;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByWishlist_Id(Long wishlistId);

    Optional<WishlistItem> findByWishlist_IdAndProduct_Id(
            Long wishlistId, Long productId);

    void deleteByWishlist_Id(Long wishlistId);
}