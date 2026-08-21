package com.mazen.ecommerce.shop_service.service;

import org.springframework.stereotype.Service;

import com.mazen.ecommerce.shop_service.model.Order;
import com.mazen.ecommerce.shop_service.model.Payment;
import com.mazen.ecommerce.shop_service.model.PaymentStatus;
import com.mazen.ecommerce.shop_service.repository.PaymentRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment processPayment(Order order) {
        // Simulate payment processing logic
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentStatus(PaymentStatus.SUCCESS); // Assuming payment is successful
        payment.setPaymentDate(new java.util.Date());
        payment.setAmount(order.getTotalPrice());
        paymentRepository.save(payment);

        return payment;
    }
}