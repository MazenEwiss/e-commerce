package com.mazen.ecommerce.shop_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mazen.ecommerce.shop_service.dto.TransactionRequestDto;
import com.mazen.ecommerce.shop_service.dto.TransactionResponseDto;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @PostMapping("/wallet/withdraw")
    ResponseEntity<TransactionResponseDto> withdraw(@RequestBody TransactionRequestDto request);
}