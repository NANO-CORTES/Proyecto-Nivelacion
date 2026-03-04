package com.reto.catalog_service.dto;

public class ProductRequestDTO {
    private String name;
    private Double price;
    private String description;
    private Integer stock;

    public ProductRequestDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
