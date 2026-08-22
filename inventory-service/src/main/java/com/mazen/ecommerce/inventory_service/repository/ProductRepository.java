package com.mazen.ecommerce.inventory_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mazen.ecommerce.inventory_service.model.Category;
import com.mazen.ecommerce.inventory_service.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods can be defined here if needed
    List<Product> findByCategory(Category category); 
    @Modifying
    @Query("UPDATE Product p SET p.quantity = p.quantity - :amount WHERE p.id = :id AND p.quantity >= :amount")
    int reserveStock(@Param("id") Long id, @Param("amount") Integer amount);
    @Modifying
    @Query("UPDATE Product p SET p.quantity = p.quantity + :amount WHERE p.id = :id")
    int restoreStock(@Param("id") Long id, @Param("amount") Integer amount);
}
