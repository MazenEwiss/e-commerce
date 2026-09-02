package com.mazen.ecommerce.shop_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazen.ecommerce.shop_service.dto.AddCartItemRequestDto;
import com.mazen.ecommerce.shop_service.dto.CartResponseDto;
import com.mazen.ecommerce.shop_service.dto.OrderResponseDto;
import com.mazen.ecommerce.shop_service.dto.UpdateQuantityDto;
import com.mazen.ecommerce.shop_service.service.CartService;
import com.mazen.ecommerce.shop_service.service.OrderService;
import com.mazen.ecommerce.shop_service.util.AuthUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;
    private final OrderService orderService;
    public CartController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addItem(@Valid @RequestBody AddCartItemRequestDto requestDto) {
        Long userId = AuthUtil.getCurrentUserId();
        CartResponseDto updatedCart = cartService.addItemToCart(userId, requestDto.getProductId(), requestDto.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> removeItemFromCart(@PathVariable Long cartItemId) {
        Long userId = AuthUtil.getCurrentUserId();
        CartResponseDto updatedCart = cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.ok(updatedCart);
    }

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart() {
        Long userId = AuthUtil.getCurrentUserId();
        CartResponseDto cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    public ResponseEntity<CartResponseDto> clearCart() {
        Long userId = AuthUtil.getCurrentUserId();
        CartResponseDto updatedCart = cartService.clearCart(userId);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> updateItemQuantity(@PathVariable Long cartItemId,@Valid @RequestBody UpdateQuantityDto quantity) {
        Long userId = AuthUtil.getCurrentUserId();
        CartResponseDto updatedCart = cartService.updateItemQuantity(userId, cartItemId, quantity.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDto> checkoutCart() {
        Long userId = AuthUtil.getCurrentUserId();
        OrderResponseDto orderResp = orderService.placeOrder(userId);
        return ResponseEntity.ok(orderResp);
    }
}

