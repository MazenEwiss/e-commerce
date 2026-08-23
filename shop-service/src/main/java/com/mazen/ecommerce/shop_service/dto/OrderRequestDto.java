package com.mazen.ecommerce.shop_service.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public class OrderRequestDto {
    @NotNull
    private List<OrderItemRequestDto> orderItems;

    public List<OrderItemRequestDto> getOrderItems() {
        return orderItems;
    }
    public void setOrderItems(List<OrderItemRequestDto> orderItems) {
        this.orderItems = orderItems;
    }
}