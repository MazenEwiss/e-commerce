package com.mazen.ecommerce.wallet_service.dto;

import java.math.BigDecimal;

public class WalletResponseDto {

    private Long walletId;
    private String walletName;
    private Long userId;
    private BigDecimal balance;

    public WalletResponseDto(Long walletId, String walletName, Long userId, BigDecimal balance) {
        this.walletId = walletId;
        this.walletName = walletName;
        this.userId = userId;
        this.balance = balance;
    }

    public Long getWalletId() {
        return walletId;
    }

    public String getWalletName() {
        return walletName;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
