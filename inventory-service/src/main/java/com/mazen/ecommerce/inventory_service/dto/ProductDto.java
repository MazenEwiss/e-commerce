package com.mazen.ecommerce.inventory_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class ProductDto {
    @NotBlank
    private String name;
    @NotNull
    private BigDecimal price;
    @NotNull
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Integer quantity; 
    private String imageUrl;
    private boolean isNew = true;
    @NotNull
    private Long categoryId;

    public ProductDto() {
    }
    public ProductDto(String name, BigDecimal price, Integer quantity, String imageUrl, Boolean isNew, Long categoryId) {

        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.isNew = isNew;
        this.categoryId = categoryId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public boolean isNew() {
        return isNew;
    }
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
    public Long getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    @Override
    public String toString() {
        return "ProductDto{" +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", imageUrl='" + imageUrl + '\'' +
                ", isNew=" + isNew +
                ", categoryId=" + categoryId +
                '}';
    }
}
