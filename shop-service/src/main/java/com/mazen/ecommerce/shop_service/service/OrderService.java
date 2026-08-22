package com.mazen.ecommerce.shop_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mazen.ecommerce.shop_service.client.InventoryClient;
import com.mazen.ecommerce.shop_service.dto.OrderItemRequestDto;
import com.mazen.ecommerce.shop_service.dto.OrderItemResponseDto;
import com.mazen.ecommerce.shop_service.dto.OrderRequestDto;
import com.mazen.ecommerce.shop_service.dto.OrderResponseDto;
import com.mazen.ecommerce.shop_service.dto.ProductResponseDto;
import com.mazen.ecommerce.shop_service.exception.OrderNotFoundException;
import com.mazen.ecommerce.shop_service.exception.OrderPlacementFailedException;
import com.mazen.ecommerce.shop_service.model.Order;
import com.mazen.ecommerce.shop_service.model.OrderItem;
import com.mazen.ecommerce.shop_service.model.OrderStatus;
import com.mazen.ecommerce.shop_service.model.Payment;
import com.mazen.ecommerce.shop_service.model.PaymentStatus;
import com.mazen.ecommerce.shop_service.repository.OrderRepository;

import feign.FeignException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final InventoryClient inventoryClient;

    public OrderService(OrderRepository orderRepository, PaymentService paymentService, InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.inventoryClient = inventoryClient;
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

    public OrderResponseDto placeOrder(OrderRequestDto requestDto) {
        Order order = new Order();
        order.setUserId(requestDto.getUserId());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(new Date());

        List<OrderItem> orderItems = new ArrayList<>();
        // Tracks items successfully reserved so far, so we can compensate (restock) if a later item fails
        List<OrderItemRequestDto> reservedItems = new ArrayList<>();

        for (OrderItemRequestDto itemDto : requestDto.getOrderItems()) {
            try {
                ResponseEntity<ProductResponseDto> response
                        = inventoryClient.purchaseProduct(itemDto.getProductId(), itemDto.getQuantity());
                ProductResponseDto product = response.getBody();

                OrderItem item = new OrderItem();
                item.setProductId(itemDto.getProductId());
                item.setQuantity(itemDto.getQuantity());
                item.setPriceAtPurchase(product.getPrice()); // real price from Inventory, not client input
                item.setOrder(order);
                orderItems.add(item);

                reservedItems.add(itemDto);
            } catch (FeignException e) {
                // Roll back every item already reserved in this attempt
                for (OrderItemRequestDto toRestore : reservedItems) {
                    try {
                        inventoryClient.restockProduct(toRestore.getProductId(), toRestore.getQuantity());
                    } catch (FeignException restoreEx) {
                        // Compensation itself failed - stock is now stuck reserved. Worth logging/alerting
                        // once real logging exists; for now the original failure still surfaces below.
                        System.err.println("Failed to restock product " + toRestore.getProductId() + ": " + restoreEx.getMessage());
                    }
                }
                throw new OrderPlacementFailedException(
                        "Could not reserve product " + itemDto.getProductId() + ": " + e.getMessage());
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
}
