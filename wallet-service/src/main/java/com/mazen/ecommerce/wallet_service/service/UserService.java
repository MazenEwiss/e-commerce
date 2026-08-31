package com.mazen.ecommerce.wallet_service.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mazen.ecommerce.wallet_service.dto.UserRequestDto;
import com.mazen.ecommerce.wallet_service.dto.UserResponseDto;
import com.mazen.ecommerce.wallet_service.exception.UserNotFoundException;
import com.mazen.ecommerce.wallet_service.model.Transaction;
import com.mazen.ecommerce.wallet_service.model.User;
import com.mazen.ecommerce.wallet_service.model.Wallet;
import com.mazen.ecommerce.wallet_service.repository.TransactionRepository;
import com.mazen.ecommerce.wallet_service.repository.UserRepository;
import com.mazen.ecommerce.wallet_service.repository.WalletRepository;

@Service
public class UserService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TransactionRepository transactionRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, WalletRepository walletRepository, JwtService jwtService, TransactionRepository transactionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletRepository = walletRepository;
        this.jwtService = jwtService;
        this.transactionRepository = transactionService;
    }

    public UserResponseDto createUser(String userName, String password, String firstName, String lastName, String email) {
        // Implement user creation logic here
        // For example, you can save the user to a database and return the created user object
        if (userRepository.existsByUserName(userName)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setUserName(userName);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        User savedUser = userRepository.save(user);   // User has no FK dependency now, saves freely

        Wallet wallet = new Wallet(savedUser);          // your existing constructor already takes a User
        Wallet savedWallet = walletRepository.save(wallet);  // Wallet's user_id FK is now valid

        savedUser.setWallet(savedWallet);   // in-memory convenience only, nothing new to persist
        return new UserResponseDto(savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail());
    }

    public UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (userRequestDto.getFirstName() != null) {
            user.setFirstName(userRequestDto.getFirstName());
        }
        if (userRequestDto.getLastName() != null) {
            user.setLastName(userRequestDto.getLastName());
        }
        if (userRequestDto.getEmail() != null) {
            user.setEmail(userRequestDto.getEmail());
        }
        if (userRequestDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }
        if (userRequestDto.getUserName() != null) {
            user.setUserName(userRequestDto.getUserName());
        }
        if (userRequestDto.getWalletId() != null) {
            throw new IllegalArgumentException("Cannot change wallet ID");
        }
        userRepository.save(user);
        return new UserResponseDto(user.getId(), user.getFirstName(), user.getLastName(), user.getUserName(), user.getEmail(), user.getWallet() != null ? user.getWallet().getWalletId() : null);
    }

    public String authenticateUser(String userName, String password) {
        // Implement user authentication logic here
        // For example, you can check the provided credentials against the stored user data
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return jwtService.generateToken(user.getId(), user.getEmail());
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Wallet wallet = user.getWallet();
        if (wallet != null) {
            if (wallet.getBalance().compareTo(java.math.BigDecimal.ZERO) > 0) {
                throw new IllegalArgumentException("Cannot delete user with non-zero wallet balance");
            }
            List<Transaction> transactions = transactionRepository
                    .findByWallet_WalletIdOrderByTimestampDesc(wallet.getWalletId());
            if (transactions != null && !transactions.isEmpty()) {
                throw new IllegalArgumentException("Cannot delete user with existing transactions");
            }
            user.setWallet(null);       // break the association first
            walletRepository.delete(wallet);
        }
        userRepository.delete(user);
    }

    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return new UserResponseDto(user.getId(), user.getFirstName(), user.getLastName(), user.getUserName(), user.getEmail(), user.getWallet() != null ? user.getWallet().getWalletId() : null);
    }
}
