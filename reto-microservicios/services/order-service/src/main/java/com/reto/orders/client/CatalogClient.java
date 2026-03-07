package com.reto.orders.client;

import com.reto.orders.dto.StockValidationResponseDTO;
import com.reto.orders.dto.ProductResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", url = "${catalog.service.url:http://localhost:8082}")
public interface CatalogClient {

    @GetMapping("/catalog/check-stock/{productId}")
    ProductResponseDTO checkStock(@PathVariable("productId") Long productId);

    @GetMapping("/catalog/products/{id}")
    ProductResponseDTO getProduct(@PathVariable("id") Long id);

}
