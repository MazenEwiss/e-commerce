package com.mazen.ecommerce.shop_service.dto;

import java.util.List;

public class CartResponseDto {
    private Long id;
    private Long userId;
    private List<CartItemResponseDto> cartItems;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<CartItemResponseDto> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemResponseDto> cartItems) {
        this.cartItems = cartItems;
    }


    
}
