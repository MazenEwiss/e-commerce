package com.mazen.ecommerce.shop_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderItemRequestDto {
    @NotNull
    private Long productId;
    @NotNull
    @Min(1)
    private Integer quantity;
    public Long getProductId() {
        return productId;
    }
    public OrderItemRequestDto() {
    }
    public OrderItemRequestDto(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
