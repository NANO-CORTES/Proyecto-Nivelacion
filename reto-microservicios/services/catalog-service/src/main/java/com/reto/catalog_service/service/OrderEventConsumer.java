package com.reto.catalog_service.service;

import com.reto.catalog_service.config.RabbitMQConfig;
import com.reto.catalog_service.dto.OrderEventDTO;
import com.reto.catalog_service.entity.ProcessedEventEntity;
import com.reto.catalog_service.repository.ProcessedEventRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeOrderEvent(OrderEventDTO event) {
        String eventId = event.getEventId();

        if (processedEventRepository.existsById(eventId)) {
            logger.info("El evento {} ya fue procesado", eventId);
            return;
        }

        logger.info("Procesando evento {}: orden {} para el producto {}", eventId, event.getOrderId(),
                event.getProductId());

        boolean success = false;
        if ("CREATED".equals(event.getStatus())) {
            success = catalogService.deductStock(event.getProductId(), event.getQuantity());
            if (success) {
                logger.info("Stock descontado para el producto {}. Candidad: {}", event.getProductId(),
                        event.getQuantity());
            } else {
                logger.warn("No se pudo descontar stock para el producto {}. Puede que no haya suficiente.",
                        event.getProductId());
            }
        } else if ("CANCELLED".equals(event.getStatus())) {
            success = catalogService.replenishStock(event.getProductId(), event.getQuantity());
            if (success) {
                logger.info("Stock repuesto para el producto {}. Candidad: {}", event.getProductId(),
                        event.getQuantity());
            } else {
                logger.warn("No se pudo reponer stock para el producto {}.", event.getProductId());
            }
        } else {
            logger.warn("Estado de evento desconocido: {}", event.getStatus());
            return;
        }

        processedEventRepository.save(new ProcessedEventEntity(eventId));
        logger.info("Evento {} marcado como procesado exitosamente.", eventId);
    }
}
