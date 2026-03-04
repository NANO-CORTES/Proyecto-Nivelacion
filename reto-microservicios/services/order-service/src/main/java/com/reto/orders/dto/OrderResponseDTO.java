package com.reto.orders.dto;

public class OrderResponseDTO {
    private String message;
    public OrderResponseDTO(String message) { this.message = message; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}