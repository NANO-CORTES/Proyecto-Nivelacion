package com.reto.catalog_service.dto;

public class ProductResponseDTO {
    private String message;

    public ProductResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}