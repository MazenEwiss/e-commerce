package com.mazen.ecommerce.wallet_service.dto;

import jakarta.validation.constraints.NotBlank;

public class WalletRequestDto {
    @NotBlank
    private String walletName;
    private Long walletId;

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }
}
