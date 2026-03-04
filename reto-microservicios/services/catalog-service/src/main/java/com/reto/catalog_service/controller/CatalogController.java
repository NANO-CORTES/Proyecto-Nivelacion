package com.reto.catalog_service.controller;

import com.reto.catalog_service.service.CatalogService;
import com.reto.catalog_service.dto.ProductResponseDTO;
import org.springframework.http.ResponseEntity;
import com.reto.catalog_service.entity.ProductEntity;
import com.reto.catalog_service.dto.ProductRequestDTO;
import com.reto.catalog_service.dto.StockValidationResponseDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @GetMapping("/ping")
    public ResponseEntity<ProductResponseDTO> ping() {
        String message = catalogService.getPingMessage();

        return ResponseEntity.ok(new ProductResponseDTO(message));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductEntity> createProduct(@RequestBody ProductRequestDTO request) {
        return ResponseEntity.ok(catalogService.createProduct(request));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductEntity>> getAllProducts() {
        return ResponseEntity.ok(catalogService.getAllProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductEntity> getProduct(@PathVariable Long id) {
        ProductEntity product = catalogService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductEntity> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO request) {
        ProductEntity product = catalogService.updateProduct(id, request);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = catalogService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/check-stock")
    public ResponseEntity<StockValidationResponseDTO> checkStock(@RequestParam Long productId,
            @RequestParam Integer quantity) {
        boolean available = catalogService.validateStock(productId, quantity);
        return ResponseEntity.ok(new StockValidationResponseDTO(available));
    }

    @PostMapping("/deduct-stock")
    public ResponseEntity<Void> deductStock(@RequestParam Long productId, @RequestParam Integer quantity) {
        boolean deducted = catalogService.deductStock(productId, quantity);
        if (deducted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/replenish-stock")
    public ResponseEntity<Void> replenishStock(@RequestParam Long productId, @RequestParam Integer quantity) {
        boolean replenished = catalogService.replenishStock(productId, quantity);
        if (replenished) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}