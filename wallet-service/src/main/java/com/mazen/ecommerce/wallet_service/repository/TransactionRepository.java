package com.mazen.ecommerce.wallet_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mazen.ecommerce.wallet_service.model.Transaction;
import com.mazen.ecommerce.wallet_service.model.Wallet;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWallet_WalletIdOrderByTimestampDesc(Long walletId);

    public List<Transaction> findByWalletInOrderByTimestampDesc(List<Wallet> wallets);
    
}
