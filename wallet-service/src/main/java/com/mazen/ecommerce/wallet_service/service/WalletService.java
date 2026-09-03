package com.mazen.ecommerce.wallet_service.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mazen.ecommerce.wallet_service.dto.TransactionRequestDto;
import com.mazen.ecommerce.wallet_service.dto.WalletRequestDto;
import com.mazen.ecommerce.wallet_service.dto.WalletResponseDto;
import com.mazen.ecommerce.wallet_service.exception.InsufficientBalanceException;
import com.mazen.ecommerce.wallet_service.exception.UserNotFoundException;
import com.mazen.ecommerce.wallet_service.exception.WalletNotFoundException;
import com.mazen.ecommerce.wallet_service.model.AccountStatus;
import com.mazen.ecommerce.wallet_service.model.Transaction;
import com.mazen.ecommerce.wallet_service.model.TransactionState;
import com.mazen.ecommerce.wallet_service.model.TransactionType;
import com.mazen.ecommerce.wallet_service.model.User;
import com.mazen.ecommerce.wallet_service.model.Wallet;
import com.mazen.ecommerce.wallet_service.repository.TransactionRepository;
import com.mazen.ecommerce.wallet_service.repository.UserRepository;
import com.mazen.ecommerce.wallet_service.repository.WalletRepository;
import com.mazen.ecommerce.wallet_service.util.AuthUtil;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public void validateUserAccountStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user.getAccountStatus() != null && user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("User account is not active. Current status: " + user.getAccountStatus());
        }
    }

    public Transaction depositToWallet(TransactionRequestDto depositRequest) {
        Long userId = AuthUtil.getCurrentUserId();
        BigDecimal amount = depositRequest.getAmount();
        var wallet = walletRepository.findById(depositRequest.getWalletId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + userId));
        validateOwnerOfWallet(userId, wallet);
        validateUserAccountStatus(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, wallet, amount);
        transaction.setTransactionState(TransactionState.COMPLETED);
        transactionRepository.save(transaction);
        return transaction;
    }

    private void validateOwnerOfWallet(Long userId, Wallet wallet) {
        if (!userId.equals(wallet.getUser().getId())) {
            throw new IllegalArgumentException("Wallet does not belong to the authenticated user");
        }
    }

    public Transaction withdrawFromWallet(TransactionRequestDto withdrawRequest) {
        Long userId = AuthUtil.getCurrentUserId();
        BigDecimal amount = withdrawRequest.getAmount();
        var wallet = walletRepository.findById(withdrawRequest.getWalletId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + userId));
        validateOwnerOfWallet(userId, wallet);
        validateUserAccountStatus(userId);
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
        validateUserAccountStatus(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Wallet> wallets = user.getWallets();
        if (wallets == null || wallets.isEmpty()) {
            throw new WalletNotFoundException("No wallets found for user: " + userId);
        }
        //get all transactions for all wallets
        List<Transaction> transactions = transactionRepository.findByWalletInOrderByTimestampDesc(wallets);
        return transactions;
    }

    @Transactional
    public List<Transaction> transferBetweenWallets(TransactionRequestDto transferRequest) {
        Long fromUserId = AuthUtil.getCurrentUserId();
        Long toUserId = transferRequest.getToUserId();
        BigDecimal amount = transferRequest.getAmount();
        Long walletId = transferRequest.getWalletId();
        Long toWalletId = transferRequest.getToWalletId();
        validateOwnerOfWallet(fromUserId, walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + fromUserId)));
        validateOwnerOfWallet(toUserId, walletRepository.findById(toWalletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + toUserId)));
        validateUserAccountStatus(fromUserId);
        validateUserAccountStatus(toUserId);
        var fromWallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + fromUserId));
        var toWallet = walletRepository.findById(toWalletId)
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
    public WalletResponseDto updateWallet(Long userId, WalletRequestDto walletRequestDto) {
        Long walletId = walletRequestDto.getWalletId();
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + userId));
        validateOwnerOfWallet(userId, wallet);
        validateUserAccountStatus(userId);
        wallet.setWalletName(walletRequestDto.getWalletName());
        walletRepository.save(wallet);
        return new WalletResponseDto(wallet.getWalletId(), wallet.getWalletName(), wallet.getUser().getId(), wallet.getBalance());
    }
    public BigDecimal getWalletBalance(Long walletId) {
        Long userId = AuthUtil.getCurrentUserId();
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + userId));
        validateOwnerOfWallet(userId, wallet);
        validateUserAccountStatus(userId);
        return wallet.getBalance();
    }

}
