package com.mazen.ecommerce.inventory_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mazen.ecommerce.inventory_service.dto.CategoryRequestDto;
import com.mazen.ecommerce.inventory_service.dto.CategoryResponseDto;
import com.mazen.ecommerce.inventory_service.exception.CategoryNotFoundException;
import com.mazen.ecommerce.inventory_service.model.Category;
import com.mazen.ecommerce.inventory_service.repository.CategoryRepository;

@Service
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        Category category = new Category();
        category.setCategoryName(categoryRequestDto.getCategoryName());
        categoryRepository.save(category);
        return new CategoryResponseDto(category.getCategoryId(), category.getCategoryName());
    }
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("Category with ID " + categoryId + " does not exist.");
        }
        categoryRepository.deleteById(categoryId);
    }
    public CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + categoryId + " does not exist."));
        if (categoryRequestDto.getCategoryName() != null) {
            category.setCategoryName(categoryRequestDto.getCategoryName());
        }
        categoryRepository.save(category);
        return new CategoryResponseDto(category.getCategoryId(), category.getCategoryName());
    }

    public CategoryResponseDto getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(category -> new CategoryResponseDto(category.getCategoryId(), category.getCategoryName()))
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + categoryId + " does not exist."));
    }
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> new CategoryResponseDto(category.getCategoryId(), category.getCategoryName()))
                .toList();
    }

}
