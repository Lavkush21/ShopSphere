package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.ProductReview;

public interface ProductReviewService {

    ProductReview addReview(
            Long customerId, Long productId, ProductReview review);

    List<ProductReview> getReviewsByProductId(Long productId);

    List<ProductReview> getReviewsByCustomerId(Long customerId);

    ProductReview updateReview(Long reviewId, ProductReview review);

    void deleteReview(Long reviewId);
}