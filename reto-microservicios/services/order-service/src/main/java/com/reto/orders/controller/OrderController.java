package com.reto.orders.controller;

import com.reto.orders.service.OrderService;
import com.reto.orders.dto.OrderResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;
import com.reto.orders.entity.OrderEntity;
import com.reto.orders.dto.OrderRequestDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/orders")

public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/ping")
    public ResponseEntity<OrderResponseDTO> ping() {
        String message = orderService.getPingMessage();
        return ResponseEntity.ok(new OrderResponseDTO(message));
    }

    @GetMapping
    public ResponseEntity<List<OrderEntity>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO request) {
        OrderEntity order = orderService.createOrder(request);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Orden cancelada y stock restituido");
    }
}