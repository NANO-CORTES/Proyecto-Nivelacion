package com.reto.orders.publisher;

import com.reto.orders.dto.OrderEventDTO;
import com.reto.orders.dto.OrderItemEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderEventPublisher {

    public static final String EXCHANGE_NAME = "orders.exchange";
    public static final String ROUTING_KEY_CREATED = "order.created";
    public static final String ROUTING_KEY_CANCELLED = "order.cancelled";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishOrderCreatedEvent(Long orderId, List<OrderItemEventDTO> items, String correlationId) {
        OrderEventDTO event = new OrderEventDTO();
        event.setEventId(UUID.randomUUID().toString());
        event.setOrderId(orderId);
        event.setItems(items);
        event.setCorrelationId(correlationId);
        event.setStatus("CREATED");

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_CREATED, event);
    }

    public void publishOrderCancelledEvent(Long orderId, List<OrderItemEventDTO> items, String correlationId) {
        OrderEventDTO event = new OrderEventDTO();
        event.setEventId(UUID.randomUUID().toString());
        event.setOrderId(orderId);
        event.setItems(items);
        event.setCorrelationId(correlationId);
        event.setStatus("CANCELLED");

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_CANCELLED, event);
    }
}
