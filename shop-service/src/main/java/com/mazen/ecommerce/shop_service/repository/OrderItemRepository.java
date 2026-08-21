package com.mazen.ecommerce.shop_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mazen.ecommerce.shop_service.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Custom query methods can be defined here if needed
    
}
