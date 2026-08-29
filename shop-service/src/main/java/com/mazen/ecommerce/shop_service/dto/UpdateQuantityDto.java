package com.mazen.ecommerce.shop_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateQuantityDto  {
    @NotNull
    @Min(0)
    private int quantity;
    public UpdateQuantityDto() {
    }
    public UpdateQuantityDto(int quantity) {
        this.quantity = quantity;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
}
