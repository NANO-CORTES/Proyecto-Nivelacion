package com.reto.orders.service;

import com.reto.orders.client.CatalogClient;
import com.reto.orders.dto.OrderRequestDTO;
import com.reto.orders.dto.ProductResponseDTO;
import com.reto.orders.dto.StockValidationResponseDTO;
import com.reto.orders.entity.OrderEntity;
import com.reto.orders.publisher.OrderEventPublisher;
import com.reto.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import feign.FeignException;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CatalogClient catalogClient;

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    public String getPingMessage() {
        return "Conexión exitosa desde el microservicio de Órdenes";
    }

    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public OrderEntity createOrder(OrderRequestDTO request) {
        ProductResponseDTO product;
        try {
            product = catalogClient.checkStock(request.getProductId());
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Este producto no existe o no está en stock.");
        }

        if (product == null) {
            throw new RuntimeException("Este producto no existe o no está en stock.");
        }

        if (product.getStock() == null || product.getStock() < request.getQuantity()) {
            throw new RuntimeException("No hay stock suficiente");
        }

        OrderEntity order = new OrderEntity();
        order.setCustomerName(request.getCustomerName());

        Double total = product.getPrice() * request.getQuantity();
        order.setTotalAmount(total);
        order.setUserId(request.getUserId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setStatus("CREATED");

        order = orderRepository.save(order);

        String correlationId = UUID.randomUUID().toString();
        orderEventPublisher.publishOrderCreatedEvent(order.getId(), order.getProductId(), order.getQuantity(),
                correlationId);

        return order;
    }

    @Transactional
    public OrderEntity cancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order no encontrada"));

        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("La orden ya está cancelada");
        }

        order.setStatus("CANCELLED");
        order = orderRepository.save(order);

        String correlationId = UUID.randomUUID().toString();
        orderEventPublisher.publishOrderCancelledEvent(order.getId(), order.getProductId(), order.getQuantity(),
                correlationId);

        return order;
    }
}