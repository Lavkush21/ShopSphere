package com.shopsphere.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shopsphere.entity.Product;

public interface ProductService {

    Product createProduct(Product product);

    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);

    // Search product by name
    Page<Product> searchProducts(String name, Pageable pageable);

    // Filter products by category
    Page<Product> getProductsByCategory(Long categoryId, Pageable pageable);

    // Filter products by price range
    Page<Product> getProductsByPriceRange(
            Double minPrice, Double maxPrice, Pageable pageable);

    // Sort products
    Page<Product> sortProducts(
            String sortBy, String direction, int page, int size);
}