package com.mazen.ecommerce.inventory_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazen.ecommerce.inventory_service.dto.ProductDto;
import com.mazen.ecommerce.inventory_service.dto.ProductPatchDto;
import com.mazen.ecommerce.inventory_service.dto.ProductResponseDto;
import com.mazen.ecommerce.inventory_service.model.Product;
import com.mazen.ecommerce.inventory_service.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        Product product = productService.getProduct(productId);
        return ResponseEntity.ok(product);
    }


    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/{productId}/purchase/{quantity}")
    public ResponseEntity<Product> purchaseProduct(@PathVariable Long productId, @PathVariable int quantity) {
        Product product = productService.purchaseProduct(productId, quantity);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/{productId}/restock/{quantity}")
    public ResponseEntity<Void> restockProduct(@PathVariable Long productId, @PathVariable int quantity) {
        productService.restockProduct(productId, quantity);
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        ProductResponseDto createdProduct = productService.createProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductPatchDto productPatchDto) {
        ProductResponseDto updatedProduct = productService.updateProduct(productId, productPatchDto);
        return ResponseEntity.ok(updatedProduct);
    }
}
