package com.mazen.ecommerce.wallet_service.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
public class Transaction {
    
    @Enumerated(EnumType.STRING)
    @NotNull
    TransactionType transactionType;
    @Enumerated(EnumType.STRING)
    @NotNull
    TransactionState transactionState;
    @ManyToOne
    @JoinColumn(name = "wallet_id", referencedColumnName = "walletId")
    @NotNull
    Wallet wallet;
    @Id @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long transactionId;

    @NotNull
    private java.time.LocalDateTime timestamp;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionState getTransactionState() {
        return transactionState;
    }

    public void setTransactionState(TransactionState transactionState) {
        this.transactionState = transactionState;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
    public java.time.LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(java.time.LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public Transaction(TransactionType transactionType, Wallet wallet, BigDecimal amount) {
        this.transactionType = transactionType;
        this.transactionState = TransactionState.PENDING;
        this.wallet = wallet;
        this.amount = amount;
        this.timestamp = java.time.LocalDateTime.now();
    }
    public Transaction() {
        this.timestamp = java.time.LocalDateTime.now();
    }
    @Override
    public String toString() {
        return "Transaction [transactionId=" + transactionId + ", transactionType=" + transactionType
                + ", transactionState=" + transactionState + ", wallet=" + wallet + ", timestamp=" + timestamp + ", amount=" + amount + "]";
    }

}
