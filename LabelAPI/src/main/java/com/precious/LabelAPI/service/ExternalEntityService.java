package com.precious.LabelAPI.service;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import reactor.core.publisher.Mono;

// Create a service to handle external entity interactions
@Service
@Slf4j
public class ExternalEntityService {
    private final WebClient taskServiceClient;
    private final WebClient clientServiceClient;
    private final CircuitBreaker circuitBreaker;

    public ExternalEntityService(
        @Value("${services.task-service.url}") String taskServiceUrl,
        @Value("${services.client-service.url}") String clientServiceUrl
    ) {
        // Initialize WebClient with retry and timeout configurations
        this.taskServiceClient = WebClient.builder()
            .baseUrl(taskServiceUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(ExchangeFilterFunctions.basicAuthentication("username", "password"))
            .build();
            
        this.clientServiceClient = WebClient.builder()
            .baseUrl(clientServiceUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(ExchangeFilterFunctions.basicAuthentication("username", "password"))
            .build();

        // Configure circuit breaker
        this.circuitBreaker = CircuitBreaker.of("external-service", CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofMillis(1000))
            .slidingWindowSize(2)
            .build());
    }

    /**
     * Fetches task information from the task service
     * Uses circuit breaker pattern to handle failures gracefully
     */
    public Mono<TaskReferenceDto> getTaskReference(UUID taskId) {
        return Mono.fromSupplier(() -> 
            circuitBreaker.executeSupplier(() -> 
                taskServiceClient.get()
                    .uri("/api/v1/tasks/{id}", taskId)
                    .retrieve()
                    .onStatus(
                        HttpStatus::is4xxClientError,
                        response -> Mono.error(new ExternalServiceException("Task Service", "Task not found: " + taskId))
                    )
                    .onStatus(
                        HttpStatus::is5xxServerError,
                        response -> Mono.error(new ExternalServiceException("Task Service", "Server error"))
                    )
                    .bodyToMono(TaskReferenceDto.class)
                    .block()
            )
        ).doOnError(e -> log.error("Error fetching task {}: {}", taskId, e.getMessage()));
    }

    /**
     * Fetches client information from the client service
     * Implements retry pattern for transient failures
     */
    public Mono<ClientReferenceDto> getClientReference(UUID clientId) {
        return clientServiceClient.get()
            .uri("/api/v1/clients/{id}", clientId)
            .retrieve()
            .onStatus(
                HttpStatus::is4xxClientError,
                response -> Mono.error(new ExternalServiceException("Client Service", "Client not found: " + clientId))
            )
            .onStatus(
                HttpStatus::is5xxServerError,
                response -> Mono.error(new ExternalServiceException("Client Service", "Server error"))
            )
            .bodyToMono(ClientReferenceDto.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
            .doOnError(e -> log.error("Error fetching client {}: {}", clientId, e.getMessage()));
    }
}
