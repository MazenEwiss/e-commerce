package com.mazen.ecommerce.shop_service.dto;

import jakarta.validation.constraints.NotNull;

public class CheckoutRequestDto {
    @NotNull
    private Long walletId;

    public CheckoutRequestDto() {
    }
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }
    
}
