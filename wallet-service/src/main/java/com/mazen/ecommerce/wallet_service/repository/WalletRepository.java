package com.mazen.ecommerce.wallet_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mazen.ecommerce.wallet_service.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    Optional<Wallet> findByUserId(Long userId);
}
