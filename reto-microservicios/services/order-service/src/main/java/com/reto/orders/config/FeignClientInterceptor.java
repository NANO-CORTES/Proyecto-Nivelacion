package com.reto.orders.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public void apply(RequestTemplate template) {
        String correlationId = MDC.get(CORRELATION_ID_MDC_KEY);
        if (correlationId != null) {
            template.header(CORRELATION_ID_HEADER, correlationId);
        }
    }
}
