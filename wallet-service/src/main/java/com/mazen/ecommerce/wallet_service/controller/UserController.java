package com.mazen.ecommerce.wallet_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazen.ecommerce.wallet_service.dto.ChangeAccountStatusRequestDto;
import com.mazen.ecommerce.wallet_service.dto.ChangeUserRoleRequestDto;
import com.mazen.ecommerce.wallet_service.dto.UserRequestDto;
import com.mazen.ecommerce.wallet_service.dto.UserResponseDto;
import com.mazen.ecommerce.wallet_service.dto.WalletRequestDto;
import com.mazen.ecommerce.wallet_service.dto.WalletResponseDto;
import com.mazen.ecommerce.wallet_service.service.UserService;
import com.mazen.ecommerce.wallet_service.service.WalletService;
import com.mazen.ecommerce.wallet_service.util.AuthUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("wallet")
public class UserController {
    private final UserService userService;
    private final WalletService walletService;
    
    public UserController(UserService userService, WalletService walletService) {
        this.userService = userService;
        this.walletService = walletService;
    }
    @GetMapping("/users")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        Long currentUserId = AuthUtil.getCurrentUserId();
        UserResponseDto userResponseDto = userService.getUserById(currentUserId);
        return ResponseEntity.ok(userResponseDto);
    }
    @PatchMapping("/users")
    public ResponseEntity<UserResponseDto> updateCurrentUser(@RequestBody UserRequestDto userRequestDto) {
        Long currentUserId = AuthUtil.getCurrentUserId();
        UserResponseDto updatedUser = userService.updateUser(currentUserId, userRequestDto);
        return ResponseEntity.ok(updatedUser);
    }
    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteCurrentUser() {
        Long currentUserId = AuthUtil.getCurrentUserId();
        userService.deleteUser(currentUserId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/admin/users/status")
    public ResponseEntity<UserResponseDto> updateCurrentUserStatus(@Valid @RequestBody ChangeAccountStatusRequestDto userRequestDto) {
        Long currentUserId = AuthUtil.getCurrentUserId();
        UserResponseDto updatedUser = userService.changeUserAccountStatus(currentUserId, userRequestDto);
        return ResponseEntity.ok(updatedUser);
    }
    @PatchMapping("/admin/users/role")
    public ResponseEntity<UserResponseDto> updateCurrentUserRole(@Valid @RequestBody ChangeUserRoleRequestDto userRequestDto) {
        Long currentUserId = AuthUtil.getCurrentUserId();
        UserResponseDto updatedUser = userService.changeUserRole(currentUserId, userRequestDto);
        return ResponseEntity.ok(updatedUser);
    }
    @PatchMapping("/users/add-wallet")
    public ResponseEntity<UserResponseDto> addWalletToCurrentUser(@Valid @RequestBody WalletRequestDto walletRequestDto) {
        Long currentUserId = AuthUtil.getCurrentUserId();
        UserResponseDto updatedUser = userService.addWalletForUser(currentUserId, walletRequestDto);
        return ResponseEntity.ok(updatedUser);
    }
        @PatchMapping("/users/{walletId}")
    public ResponseEntity<WalletResponseDto> updateWallet(@PathVariable Long walletId, @Valid @RequestBody WalletRequestDto walletRequestDto) {
        Long currentUserId = AuthUtil.getCurrentUserId();
        walletRequestDto.setWalletId(walletId);
        WalletResponseDto updatedWallet = walletService.updateWallet(currentUserId, walletRequestDto);
        return ResponseEntity.ok(updatedWallet);
    }
    @PatchMapping("/users/remove-wallet/{walletId}")
    public ResponseEntity<UserResponseDto> removeWalletFromCurrentUser(@PathVariable Long walletId) {
        Long currentUserId = AuthUtil.getCurrentUserId();
        UserResponseDto updatedUser = userService.removeWalletFromUser(currentUserId, walletId);
        return ResponseEntity.ok(updatedUser);
    }
    @GetMapping("/users/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers(AuthUtil.getCurrentUserId());
        return ResponseEntity.ok(users);
    }
}
