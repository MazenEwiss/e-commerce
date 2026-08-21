package com.mazen.ecommerce.shop_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mazen.ecommerce.shop_service.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}