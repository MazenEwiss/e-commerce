package com.mazen.ecommerce.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mazen.ecommerce.inventory_service.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c.id FROM Category c WHERE c = :category")
    Long getCategoryId(@Param("category") Category category);
}
