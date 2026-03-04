package com.reto.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable()) // Desactiva la protección contra POST no autorizados
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/auth/**").permitAll() // Permite entrar al Auth sin token
                .anyExchange().permitAll() // El resto lo manejará tu AuthenticationFilter
            )
            .build();
    }
}