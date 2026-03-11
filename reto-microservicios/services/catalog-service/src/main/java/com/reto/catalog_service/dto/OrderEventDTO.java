package com.reto.catalog_service.dto;

import java.util.List;

public class OrderEventDTO {

    private String eventId;
    private Long orderId;
    private List<OrderItemEventDTO> items;
    private String correlationId;
    private String status;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<OrderItemEventDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEventDTO> items) {
        this.items = items;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
