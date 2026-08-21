package com.mazen.ecommerce.shop_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mazen.ecommerce.shop_service.dto.CartItemResponseDto;
import com.mazen.ecommerce.shop_service.dto.CartResponseDto;
import com.mazen.ecommerce.shop_service.model.Cart;
import com.mazen.ecommerce.shop_service.model.CartItem;
import com.mazen.ecommerce.shop_service.repository.CartRepository;

@Service
public class CartService {

    private CartRepository cartRepository;

    public CartService(CartRepository cartRepo) {
        this.cartRepository = cartRepo;
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    cart.setCartItems(new ArrayList<>());
                    return cartRepository.save(cart);
                });
    }

    public CartResponseDto addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // your code: increase its quantity by `quantity`, no new CartItem needed
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // your code: build a new CartItem (productId, quantity, set its cart back-reference),
            //            add it to cart.getCartItems()
            CartItem newItem = new CartItem();
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            cart.getCartItems().add(newItem);
        }

        return toCartResponseDto(cartRepository.save(cart));
    }

    public CartResponseDto removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().removeIf(item -> item.getCartItemId().equals(cartItemId));
        return toCartResponseDto(cartRepository.save(cart));
    }

    public CartResponseDto getCart(Long userId) {
        return toCartResponseDto(getOrCreateCart(userId));
    }

    public CartResponseDto clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().clear();
        return toCartResponseDto(cartRepository.save(cart));
    }

    private CartResponseDto toCartResponseDto(Cart cart) {
        CartResponseDto dto = new CartResponseDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());

        List<CartItemResponseDto> itemDtos = cart.getCartItems().stream()
                .map(item -> {
                    CartItemResponseDto itemDto = new CartItemResponseDto();
                    itemDto.setCartItemId(item.getCartItemId());
                    itemDto.setProductId(item.getProductId());
                    itemDto.setQuantity(item.getQuantity());
                    return itemDto;
                })
                .toList();
        dto.setCartItems(itemDtos);

        return dto;
    }
    public CartResponseDto updateItemQuantity(Long userId, Long cartItemId, int newQuantity) {
    Cart cart = getOrCreateCart(userId);

    CartItem item = cart.getCartItems().stream()
            .filter(i -> i.getCartItemId().equals(cartItemId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

    // your code: set the new quantity on `item`
    item.setQuantity(newQuantity);

    return toCartResponseDto(cartRepository.save(cart));
}

}
