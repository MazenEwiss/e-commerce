package com.mazen.ecommerce.inventory_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazen.ecommerce.inventory_service.model.Category;
import com.mazen.ecommerce.inventory_service.model.Product;
import com.mazen.ecommerce.inventory_service.service.ProductService;

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

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Category category) {
        List<Product> products = productService.getProductsByCategory(category);
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
}
