package com.mazen.ecommerce.wallet_service.dto;

import com.mazen.ecommerce.wallet_service.model.UserRole;

import jakarta.validation.constraints.NotNull;

public class ChangeUserRoleRequestDto {
    @NotNull
    private Long userId;
    @NotNull
    private UserRole role;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    
}
