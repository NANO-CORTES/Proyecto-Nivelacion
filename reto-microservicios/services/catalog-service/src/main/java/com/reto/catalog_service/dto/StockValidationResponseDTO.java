package com.reto.catalog_service.dto;

public class StockValidationResponseDTO {
    private Boolean available;

    public StockValidationResponseDTO() {}

    public StockValidationResponseDTO(Boolean available) {
        this.available = available;
    }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}
