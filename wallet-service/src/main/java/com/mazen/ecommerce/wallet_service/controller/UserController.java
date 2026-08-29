package com.mazen.ecommerce.wallet_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazen.ecommerce.wallet_service.dto.UserRequestDto;
import com.mazen.ecommerce.wallet_service.dto.UserResponseDto;
import com.mazen.ecommerce.wallet_service.service.UserService;
import com.mazen.ecommerce.wallet_service.util.AuthUtil;

@RestController
@RequestMapping("wallet/users")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/mine")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        Long currentUserId = AuthUtil.getCurrentUserId();
        UserResponseDto userResponseDto = userService.getUserById(currentUserId);
        return ResponseEntity.ok(userResponseDto);
    }
    @PatchMapping("/mine")
    public ResponseEntity<UserResponseDto> updateCurrentUser(@RequestBody UserRequestDto userRequestDto) {
        Long currentUserId = AuthUtil.getCurrentUserId();
        UserResponseDto updatedUser = userService.updateUser(currentUserId, userRequestDto);
        return ResponseEntity.ok(updatedUser);
    }
    @DeleteMapping("/mine")
    public ResponseEntity<Void> deleteCurrentUser() {
        Long currentUserId = AuthUtil.getCurrentUserId();
        userService.deleteUser(currentUserId);
        return ResponseEntity.noContent().build();
    }
}
