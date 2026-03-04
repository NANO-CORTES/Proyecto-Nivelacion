package com.reto.orders.client;

import com.reto.orders.dto.StockValidationResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "catalog-service", url = "${catalog.service.url:http://localhost:8082}")
public interface CatalogClient {

    @GetMapping("/catalog/check-stock")
    StockValidationResponseDTO checkStock(@RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity);

}
