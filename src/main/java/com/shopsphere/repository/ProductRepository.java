package com.shopsphere.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByNameIgnoreCase(String name);

    // Search product by name
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Filter products by category
    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);

    // Filter products by price range
    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
}