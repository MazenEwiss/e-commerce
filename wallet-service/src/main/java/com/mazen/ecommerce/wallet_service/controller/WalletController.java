package com.mazen.ecommerce.wallet_service.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mazen.ecommerce.wallet_service.model.Transaction;
import com.mazen.ecommerce.wallet_service.service.WalletService;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }
    @PostMapping("/deposit/{userId}")
    public ResponseEntity<Transaction> depositToWallet(@PathVariable Long userId, @RequestParam BigDecimal amount) {
        Transaction transaction = walletService.depositToWallet(userId, amount);
        return ResponseEntity.ok(transaction);
    }
    @PostMapping("/withdraw/{userId}")
    public ResponseEntity<Transaction> withdrawFromWallet(@PathVariable Long userId, @RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Transaction transaction = walletService.withdrawFromWallet(userId, amount);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/transfer")
    public ResponseEntity<List<Transaction>> transferBetweenWallets(@RequestParam Long fromUserId, @
    RequestParam Long toUserId, @RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build();
        }
        List<Transaction> transaction = walletService.transferBetweenWallets(fromUserId, toUserId, amount);
        return ResponseEntity.ok(transaction);
    }
    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable Long userId) {
        List<Transaction> transactions = walletService.getTransactionHistory(userId);
        return ResponseEntity.ok(transactions);
    }
}
