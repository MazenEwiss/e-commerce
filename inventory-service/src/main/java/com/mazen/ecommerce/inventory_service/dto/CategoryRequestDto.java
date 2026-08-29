package com.mazen.ecommerce.inventory_service.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequestDto {
    @NotBlank(message = "Category name is required")
    private String categoryName;

    public CategoryRequestDto() {
    }

    public CategoryRequestDto(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
