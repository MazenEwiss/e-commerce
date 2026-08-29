package com.mazen.ecommerce.inventory_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mazen.ecommerce.inventory_service.dto.ProductDto;
import com.mazen.ecommerce.inventory_service.dto.ProductPatchDto;
import com.mazen.ecommerce.inventory_service.dto.ProductResponseDto;
import com.mazen.ecommerce.inventory_service.exception.CategoryNotFoundException;
import com.mazen.ecommerce.inventory_service.exception.InsufficientStockException;
import com.mazen.ecommerce.inventory_service.exception.ProductNotFoundException;
import com.mazen.ecommerce.inventory_service.model.Category;
import com.mazen.ecommerce.inventory_service.model.Product;
import com.mazen.ecommerce.inventory_service.repository.CategoryRepository;
import com.mazen.ecommerce.inventory_service.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
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
    public List<Product> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            throw new CategoryNotFoundException("Category not found with ID: " + categoryId);
        }
        return productRepository.findByCategory(category);
    }
    @Transactional
    public void restockProduct(Long productId, int quantity) {
        if (productRepository.restoreStock(productId, quantity) == 0) {
            throw new ProductNotFoundException("Product not found with ID: " + productId);
        }
    }
    public ProductResponseDto createProduct(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setImageUrl(productDto.getImageUrl());
        product.setNew(productDto.isNew());
        Category category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if (category == null) {
            throw new CategoryNotFoundException("Category not found with ID: " + productDto.getCategoryId());
        }
        product.setCategory(category);
        productRepository.save(product);
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getImageUrl(),
                product.isNew(),
                productDto.getCategoryId()
        );
    }
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
        productRepository.delete(product);
    }
    @Transactional
    public ProductResponseDto updateProduct(Long productId, ProductPatchDto productPatchDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
        if (productPatchDto.getName() != null) {
            product.setName(productPatchDto.getName());
        }
        if (productPatchDto.getPrice() != null) {
            product.setPrice(productPatchDto.getPrice());
        }
        if (productPatchDto.getQuantity() != null) {
            product.setQuantity(productPatchDto.getQuantity());
        }
        if (productPatchDto.getImageUrl() != null) {
            product.setImageUrl(productPatchDto.getImageUrl());
        }
        if (productPatchDto.isNew() != null) {
            product.setNew(productPatchDto.isNew());
        }
        if (productPatchDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productPatchDto.getCategoryId()).orElse(null);
            if (category == null) {
                throw new CategoryNotFoundException("Category not found with ID: " + productPatchDto.getCategoryId());
            }
            product.setCategory(category);
        }
        productRepository.save(product);
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getImageUrl(),
                product.isNew(),
                categoryRepository.getCategoryId(product.getCategory())
        );
    }
}
