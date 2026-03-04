package com.reto.catalog_service.listener;

import com.reto.catalog_service.config.RabbitMQConfig;
import com.reto.catalog_service.dto.OrderEventDTO;
import com.reto.catalog_service.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);

    @Autowired
    private CatalogService catalogService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleOrderEvent(OrderEventDTO event) {
        logger.info("Recibido evento de orden: {} para la orden {}, producto {}, cantidad {}, status {}",
                event.getEventId(), event.getOrderId(), event.getProductId(), event.getQuantity(), event.getStatus());

        try {
            if ("CREATED".equals(event.getStatus())) {
                boolean deducted = catalogService.deductStock(event.getProductId(), event.getQuantity());
                if (deducted) {
                    logger.info("Stock descontado exitosamente para el producto {}", event.getProductId());
                } else {
                    logger.warn(
                            "No se pudo descontar el stock para el producto {}, stock insuficiente o producto no existe",
                            event.getProductId());
                }
            } else if ("CANCELLED".equals(event.getStatus())) {
                boolean replenished = catalogService.replenishStock(event.getProductId(), event.getQuantity());
                if (replenished) {
                    logger.info("Stock repuesto exitosamente para el producto {}", event.getProductId());
                } else {
                    logger.warn("No se pudo reponer el stock para el producto {}", event.getProductId());
                }
            } else {
                logger.warn("Estado de evento desconocido: {}", event.getStatus());
            }
        } catch (Exception e) {
            logger.error("Error al procesar el evento de la orden: {}", e.getMessage(), e);
        }
    }
}
