package com.mazen.ecommerce.shop_service.client;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mazen.ecommerce.shop_service.dto.TransactionResponseDto;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @PostMapping("/wallet/withdraw/{userId}")
    ResponseEntity<TransactionResponseDto> withdraw(@PathVariable Long userId, @RequestParam BigDecimal amount);
}