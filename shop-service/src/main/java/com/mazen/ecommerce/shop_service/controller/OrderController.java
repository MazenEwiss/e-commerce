package com.mazen.ecommerce.shop_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazen.ecommerce.shop_service.dto.OrderResponseDto;
import com.mazen.ecommerce.shop_service.service.OrderService;
import com.mazen.ecommerce.shop_service.util.AuthUtil;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    // will be deleted in production, only for testing purposes
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getMyOrders() {
        Long userId = AuthUtil.getCurrentUserId();
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable Long id) {
        Long userId = AuthUtil.getCurrentUserId();
        return ResponseEntity.ok(orderService.cancelOrder(id, userId));
    }
}
