package com.reto.catalog_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "orders.exchange";
    public static final String QUEUE_NAME = "catalog.stock.queue";
    public static final String ROUTING_KEY_CREATED = "order.created";
    public static final String ROUTING_KEY_CANCELLED = "order.cancelled";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue catalogQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingCreated(Queue catalogQueue, TopicExchange exchange) {
        return BindingBuilder.bind(catalogQueue).to(exchange).with(ROUTING_KEY_CREATED);
    }

    @Bean
    public Binding bindingCancelled(Queue catalogQueue, TopicExchange exchange) {
        return BindingBuilder.bind(catalogQueue).to(exchange).with(ROUTING_KEY_CANCELLED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
