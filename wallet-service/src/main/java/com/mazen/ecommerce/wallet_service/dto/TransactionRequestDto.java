package com.mazen.ecommerce.wallet_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class TransactionRequestDto {
    @NotNull
    private Long walletId;
    private Long toWalletId;
    private Long toUserId;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;

    public TransactionRequestDto() {
    }
    public TransactionRequestDto(Long toUserId, BigDecimal amount) {
        this.toUserId = toUserId;
        this.amount = amount;
    }
    public Long getToUserId() {
        return toUserId;
    }
    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }
    public Long getToWalletId() {
        return toWalletId;
    }
    public void setToWalletId(Long toWalletId) {
        this.toWalletId = toWalletId;
    }
}
