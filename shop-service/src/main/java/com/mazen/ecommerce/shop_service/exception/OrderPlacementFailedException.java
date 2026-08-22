package com.mazen.ecommerce.shop_service.exception;

public class OrderPlacementFailedException extends RuntimeException {
    public OrderPlacementFailedException(String message) {
        super(message);
    }
    
}
