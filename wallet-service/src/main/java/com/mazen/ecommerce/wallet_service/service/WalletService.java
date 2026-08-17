package com.mazen.ecommerce.wallet_service.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mazen.ecommerce.wallet_service.exception.InsufficientBalanceException;
import com.mazen.ecommerce.wallet_service.exception.WalletNotFoundException;
import com.mazen.ecommerce.wallet_service.model.Transaction;
import com.mazen.ecommerce.wallet_service.model.TransactionState;
import com.mazen.ecommerce.wallet_service.model.TransactionType;
import com.mazen.ecommerce.wallet_service.repository.TransactionRepository;
import com.mazen.ecommerce.wallet_service.repository.WalletRepository;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }
    public Transaction depositToWallet(Long userId, BigDecimal amount) {
        var wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + userId));
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, wallet, amount);
        transaction.setTransactionState(TransactionState.COMPLETED);
        transactionRepository.save(transaction);
        return transaction;
    }
    public Transaction withdrawFromWallet(Long userId, BigDecimal amount) {
        var wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + userId));
        Transaction transaction = new Transaction(TransactionType.WITHDRAWAL, wallet, amount);
        if (wallet.getBalance().compareTo(amount) < 0) {
            transaction.setTransactionState(TransactionState.FAILED);
            transactionRepository.save(transaction);
            throw new InsufficientBalanceException("Insufficient balance in wallet for user: " + userId);
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
        transaction.setTransactionState(TransactionState.COMPLETED);
        transactionRepository.save(transaction);
        return transaction;
    }
    public List<Transaction> getTransactionHistory(Long userId) {
        var wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + userId));
        return transactionRepository.findByWallet_WalletIdOrderByTimestampDesc(wallet.getWalletId());
    }
    @Transactional
    public List<Transaction> transferBetweenWallets(Long fromUserId, Long toUserId, BigDecimal amount) {
        var fromWallet = walletRepository.findByUserId(fromUserId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + fromUserId));
        var toWallet = walletRepository.findByUserId(toUserId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + toUserId));
        Transaction transaction = new Transaction(TransactionType.PAYMENT, fromWallet, amount);
        if (fromWallet.getBalance().compareTo(amount) < 0) {
            transaction.setTransactionState(TransactionState.FAILED);
            transactionRepository.save(transaction);
            throw new InsufficientBalanceException("Insufficient balance in wallet for user: " + fromUserId);
        }
        Transaction toTransaction = new Transaction(TransactionType.PAYMENT, toWallet, amount);
        fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
        toWallet.setBalance(toWallet.getBalance().add(amount));
        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);
        transaction.setTransactionState(TransactionState.COMPLETED);
        transactionRepository.save(transaction);
        toTransaction.setTransactionState(TransactionState.COMPLETED);
        transactionRepository.save(toTransaction);
        return Arrays.asList(transaction, toTransaction);
    }
}
