package com.mazen.ecommerce.wallet_service.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mazen.ecommerce.wallet_service.dto.ChangeAccountStatusRequestDto;
import com.mazen.ecommerce.wallet_service.dto.ChangeUserRoleRequestDto;
import com.mazen.ecommerce.wallet_service.dto.UserRequestDto;
import com.mazen.ecommerce.wallet_service.dto.UserResponseDto;
import com.mazen.ecommerce.wallet_service.dto.WalletRequestDto;
import com.mazen.ecommerce.wallet_service.exception.UserNotFoundException;
import com.mazen.ecommerce.wallet_service.model.Transaction;
import com.mazen.ecommerce.wallet_service.model.User;
import com.mazen.ecommerce.wallet_service.model.UserRole;
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
    private final WalletService walletService;
    public UserService(WalletService walletService, UserRepository userRepository, PasswordEncoder passwordEncoder, WalletRepository walletRepository, JwtService jwtService, TransactionRepository transactionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletRepository = walletRepository;
        this.jwtService = jwtService;
        this.transactionRepository = transactionService;
        this.walletService = walletService;
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
        return convertToUserResponseDto(savedUser);
    }

    private void validateUserRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user.getRole() == UserRole.ADMIN) {
            return;
        } else {
            throw new IllegalStateException("User does not have admin privileges");
        }

    }

    public UserResponseDto addWalletForUser(Long userId, WalletRequestDto walletRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        walletService.validateUserAccountStatus(userId);
        Wallet wallet = new Wallet(user);
        wallet.setWalletName(walletRequestDto.getWalletName());
        Wallet savedWallet = walletRepository.save(wallet);
        boolean added = user.getWallets().add(savedWallet);
        if (!added) {
            throw new IllegalStateException("Failed to add wallet to user");
        }
        userRepository.save(user);
        return convertToUserResponseDto(user);
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
        return convertToUserResponseDto(user);
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
        List<Wallet> wallet = user.getWallets();
        for (Wallet w : wallet) {
            if (w.getBalance().compareTo(java.math.BigDecimal.ZERO) > 0) {
                throw new IllegalArgumentException("Cannot delete user with non-zero wallet balance");
            }
            List<Transaction> transactions = transactionRepository
                    .findByWallet_WalletIdOrderByTimestampDesc(w.getWalletId());
            if (transactions != null && !transactions.isEmpty()) {
                throw new IllegalArgumentException("Cannot delete user with existing transactions");
            }
            w.setUser(null); // break the association first
            walletRepository.delete(w);
        }
        userRepository.delete(user);
    }

    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return convertToUserResponseDto(user);
    }

    public List<UserResponseDto> getAllUsers(Long userId) {
        validateUserRole(userId);
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> convertToUserResponseDto(user))
                .toList();
    }

    public UserResponseDto changeUserRole(Long userId, ChangeUserRoleRequestDto requestDto) {
        validateUserRole(userId);
        User targetUser = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Target user not found"));
        if (requestDto.getRole() == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }
        targetUser.setRole(requestDto.getRole());
        return convertToUserResponseDto(userRepository.save(targetUser));
    }

    private UserResponseDto convertToUserResponseDto(User user) {
        return new UserResponseDto(user.getId(), user.getFirstName(), user.getLastName(), user.getUserName(), user.getEmail(), user.getWallets() != null && !user.getWallets().isEmpty() ? user.getWallets().stream().map(Wallet::getWalletId).toList() : null, user.getRole(), user.getAccountStatus());
    }

    public UserResponseDto changeUserAccountStatus(Long userId, ChangeAccountStatusRequestDto requestDto) {
        validateUserRole(userId);
        User targetUser = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Target user not found"));
        if (requestDto.getAccountStatus() == null) {
            throw new IllegalArgumentException("Account status cannot be null");
        }
        targetUser.setAccountStatus(requestDto.getAccountStatus());
        return convertToUserResponseDto(userRepository.save(targetUser));
    }

}
