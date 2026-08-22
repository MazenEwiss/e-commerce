package com.mazen.ecommerce.shop_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.mazen.ecommerce.shop_service.dto.ProductResponseDto;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    
    @PostMapping("/products/{productId}/purchase/{quantity}")
    public ResponseEntity<ProductResponseDto> purchaseProduct(@PathVariable Long productId, @PathVariable int quantity);
    @PostMapping("/products/{productId}/restock/{quantity}")
    public ResponseEntity<Void> restockProduct(@PathVariable Long productId, @PathVariable int quantity);
}
