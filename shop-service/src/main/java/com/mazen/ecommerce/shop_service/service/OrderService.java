package com.mazen.ecommerce.shop_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mazen.ecommerce.shop_service.client.InventoryClient;
import com.mazen.ecommerce.shop_service.dto.OrderItemRequestDto;
import com.mazen.ecommerce.shop_service.dto.OrderItemResponseDto;
import com.mazen.ecommerce.shop_service.dto.OrderResponseDto;
import com.mazen.ecommerce.shop_service.dto.ProductResponseDto;
import com.mazen.ecommerce.shop_service.exception.OrderNotCancellableException;
import com.mazen.ecommerce.shop_service.exception.OrderNotFoundException;
import com.mazen.ecommerce.shop_service.exception.OrderPlacementFailedException;
import com.mazen.ecommerce.shop_service.model.Cart;
import com.mazen.ecommerce.shop_service.model.Order;
import com.mazen.ecommerce.shop_service.model.OrderItem;
import com.mazen.ecommerce.shop_service.model.OrderStatus;
import com.mazen.ecommerce.shop_service.model.Payment;
import com.mazen.ecommerce.shop_service.model.PaymentStatus;
import com.mazen.ecommerce.shop_service.repository.CartRepository;
import com.mazen.ecommerce.shop_service.repository.OrderRepository;

import feign.FeignException;
import jakarta.transaction.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final InventoryClient inventoryClient;
    private final CartRepository cartRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository, CartService cartService, PaymentService paymentService, InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.inventoryClient = inventoryClient;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }

    private OrderResponseDto toOrderResponseDto(Order saved) {

        OrderResponseDto responseDto = new OrderResponseDto();
        responseDto.setOrderId(saved.getId());
        responseDto.setUserId(saved.getUserId());
        responseDto.setStatus(saved.getStatus());
        responseDto.setOrderDate(saved.getOrderDate());
        responseDto.setTotalPrice(saved.getTotalPrice());

        List<OrderItemResponseDto> orderItemDtos = saved.getOrderItems().stream()
                .map(item -> {
                    OrderItemResponseDto itemDto = new OrderItemResponseDto();
                    itemDto.setProductId(item.getProductId());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
                    return itemDto;
                })
                .toList();
        responseDto.setOrderItems(orderItemDtos);
        return responseDto;
    }

    @Transactional
    public OrderResponseDto placeOrder(Long userId) {

        Order order = cartService.getCheckoutCart(userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
        List<OrderItem> orderItems = new ArrayList<>();
        // Tracks items successfully reserved so far, so we can compensate (restock) if a later item fails
        List<OrderItemRequestDto> reservedItems = new ArrayList<>();

        for (OrderItem item : order.getOrderItems()) {
            try {
                ResponseEntity<ProductResponseDto> productResponse = inventoryClient.getProduct(item.getProductId());
                ProductResponseDto product = productResponse.getBody();
                if (product.getQuantity() < item.getQuantity()) {
                    throw new OrderPlacementFailedException("Insufficient stock for product: " + item.getProductId());
                }
                // Reserve the stock
                inventoryClient.purchaseProduct(item.getProductId(), item.getQuantity());
                reservedItems.add(new OrderItemRequestDto(item.getProductId(), item.getQuantity()));
                orderItems.add(item);
            } catch (FeignException e) {
                // If any product fails, we need to restock previously reserved items
                for (OrderItemRequestDto toRestore : reservedItems) {
                    try {
                        inventoryClient.restockProduct(toRestore.getProductId(), toRestore.getQuantity());
                    } catch (FeignException restoreEx) {
                        // Log the error; in a real application, consider retrying or alerting
                        System.err.println("Failed to restock product " + toRestore.getProductId() + ": " + restoreEx.getMessage());
                    }
                }
                throw new OrderPlacementFailedException("Failed to place order due to product issues: " + e.getMessage());
            }
        }

        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);

        Order saved = orderRepository.save(order);
        Payment payment = paymentService.processPayment(saved);
        saved.setPayment(payment);

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            saved.setStatus(OrderStatus.PROCESSING);
            cartService.clearCart(cart.getUserId()); // Clear the cart after converting to order
            cartRepository.save(cart); // Save the cleared cart
        } else {
            saved.setStatus(OrderStatus.PAYMENT_FAILED);
            // Payment failed after stock was already reserved - give it all back
            for (OrderItemRequestDto toRestore : reservedItems) {
                try {
                    inventoryClient.restockProduct(toRestore.getProductId(), toRestore.getQuantity());
                } catch (FeignException restoreEx) {
                    // same note as above
                    System.err.println("Failed to restock product " + toRestore.getProductId() + ": " + restoreEx.getMessage());
                }
            }
        }

        Order finalSaved = orderRepository.save(saved);

        return toOrderResponseDto(finalSaved);
    }

    public OrderResponseDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        return toOrderResponseDto(order);
    }

    public List<OrderResponseDto> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::toOrderResponseDto)
                .toList();
    }

    public OrderResponseDto cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new OrderNotFoundException("Order not found for this user with id: " + orderId);
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PROCESSING) {
            throw new OrderNotCancellableException("Order cannot be cancelled in its current status: " + order.getStatus());
        }

        // Restock the items
        for (OrderItem item : order.getOrderItems()) {
            try {
                inventoryClient.restockProduct(item.getProductId(), item.getQuantity());
            } catch (FeignException e) {
                // Log the error; in a real application, consider retrying or alerting
                System.err.println("Failed to restock product " + item.getProductId() + ": " + e.getMessage());
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        return toOrderResponseDto(saved);
    }
}
