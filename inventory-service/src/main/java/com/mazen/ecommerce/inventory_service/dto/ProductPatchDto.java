package com.mazen.ecommerce.inventory_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;



public class ProductPatchDto {

    private String name;
    private BigDecimal price;
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Integer quantity; 
    private String imageUrl;
    private Boolean isNew ;
    private Long categoryId;

    public ProductPatchDto() {
    }
    public ProductPatchDto(String name, BigDecimal price, Integer quantity, String imageUrl, Boolean isNew, Long categoryId) {

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
    public Boolean isNew() {
        return isNew;
    }
    public void setNew(Boolean isNew) {
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
        return "ProductPatchDto{" +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", imageUrl='" + imageUrl + '\'' +
                ", isNew=" + isNew +
                ", categoryId=" + categoryId +
                '}';
    }
}
