package com.reto.catalog_service.dto;

public class OrderItemEventDTO {
    private Long productId;
    private Integer quantity;

    public OrderItemEventDTO() {
    }

    public OrderItemEventDTO(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
