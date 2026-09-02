package com.mazen.ecommerce.inventory_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazen.ecommerce.inventory_service.dto.CategoryRequestDto;
import com.mazen.ecommerce.inventory_service.dto.CategoryResponseDto;
import com.mazen.ecommerce.inventory_service.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @PostMapping("")
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        CategoryResponseDto createdCategory = categoryService.createCategory(categoryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        CategoryResponseDto updatedCategory = categoryService.updateCategory(categoryId, categoryRequestDto);
        return ResponseEntity.ok(updatedCategory);
    }
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long categoryId) {
        CategoryResponseDto category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }
    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok().body(categoryService.getAllCategories());
    }


}
