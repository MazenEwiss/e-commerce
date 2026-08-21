package com.mazen.ecommerce.shop_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mazen.ecommerce.shop_service.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Define methods for payment-related database operations here
    
}
