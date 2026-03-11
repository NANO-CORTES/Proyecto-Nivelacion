package com.reto.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
            // Mutate request with correlation ID
            exchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header(CORRELATION_ID_HEADER, correlationId)
                            .build())
                    .build();
            logger.info("Generated new CorrelationId: {} for {}", correlationId, exchange.getRequest().getURI());
        } else {
            logger.info("Retained existing CorrelationId: {} for {}", correlationId, exchange.getRequest().getURI());
        }

        // Add correlation ID to response headers
        final String fCorrelationId = correlationId;
        exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, fCorrelationId);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Run before AuthenticationFilter (which is typical)
        return -1;
    }
}
