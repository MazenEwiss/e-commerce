package com.mazen.ecommerce.shop_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mazen.ecommerce.shop_service.client.InventoryClient;
import com.mazen.ecommerce.shop_service.dto.CartItemResponseDto;
import com.mazen.ecommerce.shop_service.dto.CartResponseDto;
import com.mazen.ecommerce.shop_service.dto.ProductResponseDto;
import com.mazen.ecommerce.shop_service.exception.ProductNotFoundException;
import com.mazen.ecommerce.shop_service.model.Cart;
import com.mazen.ecommerce.shop_service.model.CartItem;
import com.mazen.ecommerce.shop_service.model.Order;
import com.mazen.ecommerce.shop_service.model.OrderItem;
import com.mazen.ecommerce.shop_service.model.OrderStatus;
import com.mazen.ecommerce.shop_service.repository.CartItemRepository;
import com.mazen.ecommerce.shop_service.repository.CartRepository;
import com.mazen.ecommerce.shop_service.repository.OrderItemRepository;
import com.mazen.ecommerce.shop_service.repository.OrderRepository;

import feign.FeignException;
import jakarta.transaction.Transactional;

@Service
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryClient inventoryClient;
    private final OrderRepository orderRepository;

    public CartService(CartRepository cartRepo, OrderRepository orderRepo, InventoryClient inventoryClient , CartItemRepository cartItemRepo, OrderItemRepository orderItemRepo ,OrderItemRepository orderItemRepository) {
        this.cartRepository = cartRepo;
        this.inventoryClient = inventoryClient;
        this.cartItemRepository = cartItemRepo;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepo;
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    cart.setCartItems(new ArrayList<>());
                    cart.setTotalPrice(BigDecimal.ZERO);
                    return cartRepository.save(cart);
                });
    }

    public CartResponseDto addItemToCart(Long userId, Long productId, int quantity) {
        try {
            inventoryClient.getProduct(productId);
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException("Product not found: " + productId);
        }
        ResponseEntity<ProductResponseDto> productResponse = inventoryClient.getProduct(productId);
        Cart cart = getOrCreateCart(userId);
        ProductResponseDto product = productResponse.getBody();
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
            newItem.setPriceAtPurchase(product.getPrice());
            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }
        cart.setTotalPrice(cart.getCartItems().stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        cartRepository.save(cart);
        return toCartResponseDto(cartRepository.save(cart));
    }

    public CartResponseDto removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().removeIf(item -> item.getCartItemId().equals(cartItemId));
        CartItem itemToRemove = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));
        cartItemRepository.delete(itemToRemove);
        cart.setTotalPrice(cart.getCartItems().stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        cartRepository.save(cart);
        return toCartResponseDto(cartRepository.save(cart));
    }

    public CartResponseDto getCart(Long userId) {
        return toCartResponseDto(getOrCreateCart(userId));
    }

    public CartResponseDto clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
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
                    itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
                    return itemDto;
                })
                .toList();
        dto.setCartItems(itemDtos);
        dto.setTotalPrice(cart.getTotalPrice());
        return dto;
    }

    public CartResponseDto updateItemQuantity(Long userId, Long cartItemId, int newQuantity) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cart.getCartItems().stream()
                .filter(i -> i.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        // your code: set the new quantity on `item`
        int quantity =newQuantity ;
        item.setQuantity(quantity);
        cart.setTotalPrice(cart.getCartItems().stream()
                .map(i -> i.getPriceAtPurchase().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        cartRepository.save(cart);
        return toCartResponseDto(cartRepository.save(cart));
    } 

    private Order convertToOrder(Cart cart) {
        Order order = new Order();
        order.setUserId(cart.getUserId());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(new Date());
        order.setTotalPrice(cart.getTotalPrice());
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getPriceAtPurchase());
            orderItems.add(orderItem);
            orderItem.setOrder(order);
            totalPrice = totalPrice.add(cartItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(new Date());
        orderRepository.save(order);
        return order;
    }
    @Transactional
    public Order getCheckoutCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty for user: " + userId);
        }
        Order order = convertToOrder(cart);
        return order;
    }

}
