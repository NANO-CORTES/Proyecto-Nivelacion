package com.reto.orders.service;

import com.reto.orders.client.CatalogClient;
import com.reto.orders.dto.OrderItemRequestDTO;
import com.reto.orders.dto.OrderItemEventDTO;
import com.reto.orders.dto.OrderRequestDTO;
import com.reto.orders.dto.ProductResponseDTO;
import com.reto.orders.entity.OrderEntity;
import com.reto.orders.entity.OrderItemEntity;
import com.reto.orders.publisher.OrderEventPublisher;
import com.reto.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "La orden debe contener al menos un producto.");
        }

        OrderEntity order = new OrderEntity();
        order.setCustomerName(request.getCustomerName());
        order.setUserId(request.getUserId());
        order.setStatus("CREATED");
        double totalAmount = 0.0;

        List<OrderItemEventDTO> eventItems = new ArrayList<>();

        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            ProductResponseDTO product;
            try {
                product = catalogClient.checkStock(itemRequest.getProductId());
            } catch (FeignException.NotFound e) {
                throw new RuntimeException("El producto con ID " + itemRequest.getProductId() + " no existe o no está en stock.");
            }

            if (product == null) {
                throw new RuntimeException("El producto con ID " + itemRequest.getProductId() + " no existe o no está en stock.");
            }

            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST, "La cantidad solicitada debe ser mayor a cero.");
            }

            if (product.getStock() == null || product.getStock() < itemRequest.getQuantity()) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.CONFLICT, "No hay stock suficiente para el producto: " + product.getName());
            }

            double itemTotal = product.getPrice() * itemRequest.getQuantity();
            totalAmount += itemTotal;

            OrderItemEntity orderItem = new OrderItemEntity(order, itemRequest.getProductId(), itemRequest.getQuantity(), product.getPrice());
            order.addItem(orderItem);

            eventItems.add(new OrderItemEventDTO(itemRequest.getProductId(), itemRequest.getQuantity()));
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        String correlationId = UUID.randomUUID().toString();
        orderEventPublisher.publishOrderCreatedEvent(order.getId(), eventItems, correlationId);

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

        List<OrderItemEventDTO> eventItems = order.getItems().stream()
                .map(item -> new OrderItemEventDTO(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());

        String correlationId = UUID.randomUUID().toString();
        orderEventPublisher.publishOrderCancelledEvent(order.getId(), eventItems, correlationId);

        return order;
    }
}