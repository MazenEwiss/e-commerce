package com.mazen.ecommerce.wallet_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mazen.ecommerce.wallet_service.model.User;
import com.mazen.ecommerce.wallet_service.model.Wallet;
import com.mazen.ecommerce.wallet_service.repository.UserRepository;
import com.mazen.ecommerce.wallet_service.repository.WalletRepository;

@Service
public class UserService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, WalletRepository walletRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletRepository = walletRepository;
        this.jwtService = jwtService;
    }

    public User createUser(String userName, String password, String firstName, String lastName, String email) {
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
        return savedUser;
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
}
