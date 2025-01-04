package com.precious.LabelAPI.config;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

@Configuration
public class ExternalServiceConfig {

    @Value("${services.task-service.url}")
    private String taskServiceUrl;

    @Value("${services.client-service.url}")
    private String clientServiceUrl;

    @Value("${services.username}")
    private String username;

    @Value("${services.password}")
    private String password;

    @Bean
    public WebClient taskServiceClient() {
        return WebClient.builder()
                .baseUrl(taskServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(ExchangeFilterFunctions.basicAuthentication(username, password))
                .build();
    }

    @Bean
    public WebClient clientServiceClient() {
        return WebClient.builder()
                .baseUrl(clientServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(ExchangeFilterFunctions.basicAuthentication(username, password))
                .build();
    }

    @Bean
    public CircuitBreaker circuitBreaker() {
        return CircuitBreaker.of("external-service", CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .slidingWindowSize(2)
                .build());
    }
}

