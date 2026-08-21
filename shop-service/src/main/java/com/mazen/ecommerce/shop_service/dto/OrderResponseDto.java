package com.mazen.ecommerce.shop_service.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mazen.ecommerce.shop_service.model.OrderStatus;
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private OrderStatus status;
    private Date orderDate;
    private BigDecimal totalPrice;
    private List<OrderItemResponseDto> orderItems;
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    public Date getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    public List<OrderItemResponseDto> getOrderItems() {
        return orderItems;
    }
    public void setOrderItems(List<OrderItemResponseDto> orderItems) {
        this.orderItems = orderItems;
    }

    
}
