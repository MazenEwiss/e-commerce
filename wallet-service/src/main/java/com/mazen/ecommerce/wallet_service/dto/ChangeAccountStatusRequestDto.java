package com.mazen.ecommerce.wallet_service.dto;

import com.mazen.ecommerce.wallet_service.model.AccountStatus;

import jakarta.validation.constraints.NotNull;

public class ChangeAccountStatusRequestDto {
    @NotNull
    private Long userId;
    @NotNull
    private AccountStatus accountStatus;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
}
