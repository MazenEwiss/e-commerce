package com.mazen.ecommerce.inventory_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mazen.ecommerce.inventory_service.exception.InsufficientStockException;
import com.mazen.ecommerce.inventory_service.exception.ProductNotFoundException;
import com.mazen.ecommerce.inventory_service.model.Category;
import com.mazen.ecommerce.inventory_service.model.Product;
import com.mazen.ecommerce.inventory_service.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Transactional
     public Product purchaseProduct(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        if (productRepository.reserveStock(productId, quantity) == 0) {
            throw new InsufficientStockException("Insufficient stock for product: " + productId);
        }
        product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
        return product;
    }
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
    }
    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }
    @Transactional
    public void restockProduct(Long productId, int quantity) {
        if (productRepository.restoreStock(productId, quantity) == 0) {
            throw new ProductNotFoundException("Product not found with ID: " + productId);
        }
    }
}