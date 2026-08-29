package com.mazen.ecommerce.wallet_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mazen.ecommerce.wallet_service.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    Optional<Wallet> findByUserId(Long userId);
    @Query("SELECT w.id FROM Wallet w WHERE w.user.id = :userId")
    public Long getWalletIdByUserId(Long userId);
    
}
