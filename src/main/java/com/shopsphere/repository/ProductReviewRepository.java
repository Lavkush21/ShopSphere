package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.entity.ProductReview;

@Repository
public interface ProductReviewRepository
        extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findByProduct_Id(Long productId);

    List<ProductReview> findByCustomer_Id(Long customerId);

    Optional<ProductReview> findByCustomer_IdAndProduct_Id(
            Long customerId, Long productId);

    boolean existsByCustomer_IdAndProduct_Id(
            Long customerId, Long productId);
}