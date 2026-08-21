package com.mazen.ecommerce.shop_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mazen.ecommerce.shop_service.dto.AddCartItemRequestDto;
import com.mazen.ecommerce.shop_service.dto.CartResponseDto;
import com.mazen.ecommerce.shop_service.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
@PostMapping("/{userId}/items")
public ResponseEntity<CartResponseDto> addItem(@PathVariable Long userId, @Valid @RequestBody AddCartItemRequestDto requestDto) {
    CartResponseDto updatedCart = cartService.addItemToCart(userId, requestDto.getProductId(), requestDto.getQuantity());
    return ResponseEntity.ok(updatedCart);
} 
    @DeleteMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> removeItemFromCart(@PathVariable Long userId, @PathVariable @Valid Long cartItemId) {
        CartResponseDto updatedCart = cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.ok(updatedCart);
    }
    @GetMapping("{userId}")
    public ResponseEntity<CartResponseDto> getCart(@PathVariable  Long userId) {
        CartResponseDto cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }
    @DeleteMapping("{userId}")
    public ResponseEntity<CartResponseDto> clearCart(@PathVariable Long userId) {
        CartResponseDto updatedCart = cartService.clearCart(userId);
        return ResponseEntity.ok(updatedCart);
    }
    @PutMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> updateItemQuantity(@PathVariable Long userId, @PathVariable Long cartItemId, @RequestParam int quantity) {
    // your code
        CartResponseDto updatedCart = cartService.updateItemQuantity(userId, cartItemId, quantity);
        return ResponseEntity.ok(updatedCart);
    }
}

