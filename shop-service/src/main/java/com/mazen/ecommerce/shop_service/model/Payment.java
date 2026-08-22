package com.mazen.ecommerce.shop_service.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    @OneToOne
    @JoinColumn(name = "order_id")
    @NotNull
    private Order order;
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus paymentStatus;
    @NotNull
    private Date paymentDate;
    @NotNull
    @DecimalMin(value = "0.00", message = "Amount must be a positive value")
    private BigDecimal amount;
    private Long walletTransactionId;

    public Payment() {
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getWalletTransactionId() {
        return walletTransactionId;
    }

    public void setWalletTransactionId(Long walletTransactionId) {
        this.walletTransactionId = walletTransactionId;
    }
}
