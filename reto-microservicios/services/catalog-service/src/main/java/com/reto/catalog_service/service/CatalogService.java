package com.reto.catalog_service.service;

import com.reto.catalog_service.entity.ProductEntity;
import com.reto.catalog_service.repository.ProductRepository;
import com.reto.catalog_service.dto.ProductRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {

    @Autowired
    private ProductRepository productRepository;

    public String getPingMessage() {
        return "Conexión exitosa desde el microservicio de Catálogo";
    }

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public ProductEntity getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public ProductEntity createProduct(ProductRequestDTO request) {
        ProductEntity product = new ProductEntity();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setStock(request.getStock());
        return productRepository.save(product);
    }

    public ProductEntity updateProduct(Long id, ProductRequestDTO request) {
        return productRepository.findById(id).map(product -> {
            if (request.getName() != null)
                product.setName(request.getName());
            if (request.getPrice() != null)
                product.setPrice(request.getPrice());
            if (request.getDescription() != null)
                product.setDescription(request.getDescription());
            if (request.getStock() != null)
                product.setStock(request.getStock());
            return productRepository.save(product);
        }).orElse(null);
    }

    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean validateStock(Long productId, Integer quantity) {
        return productRepository.findById(productId)
                .map(product -> product.getStock() >= quantity)
                .orElse(false);
    }

    public boolean deductStock(Long productId, Integer quantity) {
        return productRepository.findById(productId).map(product -> {
            if (product.getStock() >= quantity) {
                product.setStock(product.getStock() - quantity);
                productRepository.save(product);
                return true;
            }
            return false;
        }).orElse(false);
    }

    public boolean replenishStock(Long productId, Integer quantity) {
        return productRepository.findById(productId).map(product -> {
            product.setStock(product.getStock() + quantity);
            productRepository.save(product);
            return true;
        }).orElse(false);
    }
}