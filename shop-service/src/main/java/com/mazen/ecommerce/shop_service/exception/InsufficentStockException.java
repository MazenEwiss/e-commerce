package com.mazen.ecommerce.shop_service.exception;

public class InsufficentStockException extends RuntimeException {
    public InsufficentStockException(String message) {
        super(message);
    }
    
}
