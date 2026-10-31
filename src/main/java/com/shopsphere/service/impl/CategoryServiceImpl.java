package com.shopsphere.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopsphere.entity.Category;
import com.shopsphere.repository.CategoryRepository;
import com.shopsphere.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Create Category
    @Override
    public Category createCategory(Category category) {

        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new IllegalArgumentException("Category already exists");
        }

        return categoryRepository.save(category);
    }

    // Get All Categories
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Get Category By ID
    @Override
    public Category getCategoryById(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    // Update Category
    @Override
    public Category updateCategory(Long id, Category category) {

        Category existingCategory = getCategoryById(id);

        if (!existingCategory.getName().equalsIgnoreCase(category.getName())
                && categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setActive(category.getActive());

        return categoryRepository.save(existingCategory);
    }

    // Delete Category
    @Override
    public void deleteCategory(Long id) {

        Category category = getCategoryById(id);

        categoryRepository.delete(category);
    }
}