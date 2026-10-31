package com.shopsphere.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.Customer;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.ProductReview;
import com.shopsphere.repository.CustomerRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.ProductReviewRepository;
import com.shopsphere.service.ProductReviewService;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public ProductReviewServiceImpl(
            ProductReviewRepository reviewRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository) {

        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ProductReview addReview(
            Long customerId, Long productId, ProductReview review) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (reviewRepository.existsByCustomer_IdAndProduct_Id(
                customerId, productId)) {
            throw new RuntimeException("You have already reviewed this product");
        }

        if (review.getRating() == null
                || review.getRating() < 1
                || review.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        review.setCustomer(customer);
        review.setProduct(product);

        return reviewRepository.save(review);
    }

    @Override
    public List<ProductReview> getReviewsByProductId(Long productId) {

        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return reviewRepository.findByProduct_Id(productId);
    }

    @Override
    public List<ProductReview> getReviewsByCustomerId(Long customerId) {

        customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return reviewRepository.findByCustomer_Id(customerId);
    }

    @Override
    @Transactional
    public ProductReview updateReview(Long reviewId, ProductReview review) {

        ProductReview existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (review.getRating() == null
                || review.getRating() < 1
                || review.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        existingReview.setRating(review.getRating());
        existingReview.setComment(review.getComment());

        return reviewRepository.save(existingReview);
    }

    @Override
    public void deleteReview(Long reviewId) {

        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        reviewRepository.delete(review);
    }
}