package com.mazen.ecommerce.shop_service.service;

import org.springframework.stereotype.Service;

import com.mazen.ecommerce.shop_service.client.WalletClient;
import com.mazen.ecommerce.shop_service.dto.TransactionRequestDto;
import com.mazen.ecommerce.shop_service.model.Order;
import com.mazen.ecommerce.shop_service.model.Payment;
import com.mazen.ecommerce.shop_service.repository.PaymentRepository;

import feign.FeignException;
import jakarta.transaction.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WalletClient walletClient;

    public PaymentService(PaymentRepository paymentRepository, WalletClient walletClient) {
        this.paymentRepository = paymentRepository;
        this.walletClient = walletClient;
    }

    @Transactional
    public Payment processPayment(Order order) {
        // Simulate payment processing logic
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentDate(new java.util.Date());
        payment.setAmount(order.getTotalPrice());
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAmount(order.getTotalPrice());
        try {
            var response = walletClient.withdraw(request);
            if (response.getBody() != null) {
                if (response.getStatusCode().is2xxSuccessful()) {
                    payment.setPaymentStatus(com.mazen.ecommerce.shop_service.model.PaymentStatus.SUCCESS);
                    payment.setWalletTransactionId(response.getBody().getTransactionId());
                } else {
                    payment.setPaymentStatus(com.mazen.ecommerce.shop_service.model.PaymentStatus.FAILED);
                }
            }
        } catch (FeignException e) {
            payment.setPaymentStatus(com.mazen.ecommerce.shop_service.model.PaymentStatus.FAILED);
        }
        paymentRepository.save(payment);
        return payment;
    }
}
