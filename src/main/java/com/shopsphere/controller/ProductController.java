package com.shopsphere.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.Product;
import com.shopsphere.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Create Product
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@Valid @RequestBody Product product) {
        return productService.createProduct(product);
    }

    // Get All Products
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Get Product By ID
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // Update Product
    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    // Delete Product
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    // Search Products by Name with Pagination
    @GetMapping("/search")
    public Page<Product> searchProducts(
            @RequestParam String name,
            Pageable pageable) {
        return productService.searchProducts(name, pageable);
    }

    // Filter Products by Category
    @GetMapping("/category/{categoryId}")
    public Page<Product> getProductsByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {
        return productService.getProductsByCategory(categoryId, pageable);
    }

    // Filter Products by Price Range
    @GetMapping("/price-range")
    public Page<Product> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            Pageable pageable) {
        return productService.getProductsByPriceRange(
                minPrice, maxPrice, pageable);
    }
}