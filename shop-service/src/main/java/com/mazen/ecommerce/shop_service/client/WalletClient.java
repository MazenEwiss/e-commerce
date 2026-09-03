package com.mazen.ecommerce.shop_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mazen.ecommerce.shop_service.dto.TransactionRequestDto;
import com.mazen.ecommerce.shop_service.dto.TransactionResponseDto;
import com.mazen.ecommerce.shop_service.dto.UserResponseDto;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @PostMapping("/wallet/withdraw")
    ResponseEntity<TransactionResponseDto> withdraw(@RequestBody TransactionRequestDto request);
    @GetMapping("wallet/users")
    public ResponseEntity<UserResponseDto> getCurrentUser();
}