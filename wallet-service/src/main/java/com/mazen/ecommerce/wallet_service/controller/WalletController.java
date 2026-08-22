package com.mazen.ecommerce.wallet_service.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mazen.ecommerce.wallet_service.dto.LoginRequestDto;
import com.mazen.ecommerce.wallet_service.dto.SignupRequestDto;
import com.mazen.ecommerce.wallet_service.dto.TokenResponseDto;
import com.mazen.ecommerce.wallet_service.model.Transaction;
import com.mazen.ecommerce.wallet_service.service.UserService;
import com.mazen.ecommerce.wallet_service.service.WalletService;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    private final WalletService walletService;
    private final UserService userService;
    public WalletController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
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
    @PostMapping("/auth/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto signupRequest) {
        // Implement user signup logic here
        userService.createUser(
            signupRequest.getUserName(),
            signupRequest.getPassword(),
            signupRequest.getFirstName(),
            signupRequest.getLastName(),
            signupRequest.getEmail()
        );

        return ResponseEntity.ok("User created successfully " + signupRequest.getUserName());
    }
    @PostMapping("/auth/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        // Implement user login logic here
        String token = userService.authenticateUser(loginRequest.getUserName(), loginRequest.getPassword());
        return ResponseEntity.ok(new TokenResponseDto(token));
    }
}
