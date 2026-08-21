package com.mazen.ecommerce.shop_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mazen.ecommerce.shop_service.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Custom query methods can be defined here if needed
    List<Order> findByUserId(Long userId);
}
