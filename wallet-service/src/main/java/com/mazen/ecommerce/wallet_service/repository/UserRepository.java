package com.mazen.ecommerce.wallet_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mazen.ecommerce.wallet_service.model.User;

public interface UserRepository extends  JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
}
