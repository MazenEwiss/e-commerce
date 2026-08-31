package com.mazen.ecommerce.wallet_service.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazen.ecommerce.wallet_service.dto.LoginRequestDto;
import com.mazen.ecommerce.wallet_service.dto.SignupRequestDto;
import com.mazen.ecommerce.wallet_service.dto.TokenResponseDto;
import com.mazen.ecommerce.wallet_service.dto.TransactionRequestDto;
import com.mazen.ecommerce.wallet_service.model.Transaction;
import com.mazen.ecommerce.wallet_service.service.UserService;
import com.mazen.ecommerce.wallet_service.service.WalletService;
import com.mazen.ecommerce.wallet_service.util.AuthUtil;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;

    public WalletController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    @PostMapping("/deposit/mine")
    public ResponseEntity<Transaction> depositToWallet(@RequestBody TransactionRequestDto transferRequest) {
        BigDecimal amount = transferRequest.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Transaction transaction = walletService.depositToWallet(AuthUtil.getCurrentUserId(), amount);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/withdraw/mine")
    public ResponseEntity<Transaction> withdrawFromWallet(@RequestBody TransactionRequestDto transferRequest) {
        BigDecimal amount = transferRequest.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Transaction transaction = walletService.withdrawFromWallet(AuthUtil.getCurrentUserId(), amount);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/transfer")
    public ResponseEntity<List<Transaction>> transferBetweenWallets(@RequestBody TransactionRequestDto transferRequest ) {
        BigDecimal amount = transferRequest.getAmount();
        Long toUserId = transferRequest.getToUserId();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Long fromUserId = AuthUtil.getCurrentUserId();
        List<Transaction> transaction = walletService.transferBetweenWallets(fromUserId, toUserId, amount);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/transactions/mine")
    public ResponseEntity<List<Transaction>> getTransactionHistory() {
        List<Transaction> transactions = walletService.getTransactionHistory(AuthUtil.getCurrentUserId());
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
