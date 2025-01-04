package com.precious.LabelAPI.service;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.reactive.function.client.WebClient;

import com.precious.LabelAPI.dto.ClientReferenceDto;
import com.precious.LabelAPI.dto.TaskReferenceDto;
import com.precious.LabelAPI.exceptions.ExternalServiceException;
import com.precious.LabelAPI.exceptions.UnauthorizedRequestException;
import com.precious.LabelAPI.model.UserRole;

import java.time.Duration;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import reactor.util.retry.Retry;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatusCode;

import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Mono;

// Create a service to handle external entity interactions
@Service
@Slf4j
public class ExternalEntityService {
	private final WebClient taskServiceClient;
	private final WebClient clientServiceClient;
	private final CircuitBreaker circuitBreaker;

	public ExternalEntityService(@Qualifier("taskServiceClient") WebClient taskServiceClient,
			@Qualifier("clientServiceClient") WebClient clientServiceClient, CircuitBreaker circuitBreaker) {
		this.taskServiceClient = taskServiceClient;
		this.clientServiceClient = clientServiceClient;
		this.circuitBreaker = circuitBreaker;
	}

	/**
	 * Fetches task information from the task service
	 * Uses circuit breaker pattern to handle failures gracefully
	 *
	 * @param taskId the ID of the task to fetch
	 * @return a Mono containing the task reference DTO
	 * @throws ExternalServiceException if the task service is unavailable
	 * @throws ExternalServiceException if the task is not found
	 * @throws ExternalServiceException if the task service returns a server error
	 */
	public Mono<TaskReferenceDto> getTaskReference(UUID taskId) {
		/**
		 * This creates a Mono using a supplier
		 * 
		 * A Mono is a reactive type that represents a single value or an error
		 * In this case, the Mono will contain the result of the task service call
		 *
		 * A supplier is a functional interface that takes no arguments and
		 * returns a value
		 * In this case, the supplier calls the task service to fetch the task reference
		 * DTO
		 * The supplier is executed when the Mono is subscribed to
		 * This allows the task service call to be made asynchronously
		 * The result of the task service call is wrapped in a Mono
		 */
		return Mono.fromSupplier(() -> circuitBreaker.executeSupplier(() -> taskServiceClient.get()
				.uri("/api/v1/tasks/{id}", taskId)
				.retrieve()

				/**
				 * Handle specific HTTP status codes returned by the external service
				 * In this case, handle 4xx client errors and 5xx server errors
				 *
				 * 4xx client errors indicate that the request was invalid or
				 * the resource was not found (400 or 404)
				 *
				 * 5xx server errors indicate that the external service encountered an
				 * error(500)
				 * and was unable to process the request
				 */
				.onStatus(
						HttpStatusCode::is4xxClientError,
						response -> Mono
								.error(new ExternalServiceException("Task Service", "Task not found: " + taskId)))
				.onStatus(
						HttpStatusCode::is5xxServerError,
						response -> Mono.error(new ExternalServiceException("Task Service", "Server error")))
				// Convert the response body to a TaskReferenceDto
				.bodyToMono(TaskReferenceDto.class)
				/**
				 * Retry the request in case of transient failures
				 *
				 * Blocking means that the current thread will wait for the result
				 * This is done to ensure that the task reference DTO is available
				 * before proceeding with the next step
				 * In this case, the task reference DTO is required to fetch the client
				 * information
				 */
				.block())
		// Log any errors that occur during the task service call
		).doOnError(e -> log.error("Error fetching task {}: {}", taskId, e.getMessage()));
	}

	/**
	 * Fetches client information from the client service
	 * Implements retry pattern for transient failures
	 *
	 * @param clientId the ID of the client to fetch
	 * @return a Mono containing the client reference DTO
	 * @throws ExternalServiceException if the client service is unavailable
	 * @throws ExternalServiceException if the client is not found
	 * @throws ExternalServiceException if the client service returns a server error
	 *
	 *                                  The method fetches client information from
	 *                                  the client service
	 *                                  Same logic as getTaskReference method
	 */
	public Mono<ClientReferenceDto> getClientReference(UUID clientId) {
		return clientServiceClient.get()
				.uri("/api/v1/clients/{id}", clientId)
				.retrieve()
				.onStatus(
						HttpStatusCode::is4xxClientError,
						response -> Mono
								.error(new ExternalServiceException("Client Service", "Client not found: " + clientId)))
				.onStatus(
						HttpStatusCode::is5xxServerError,
						response -> Mono.error(new ExternalServiceException("Client Service", "Server error")))
				.bodyToMono(ClientReferenceDto.class)
				.flatMap(client -> {
					log.debug("Client role: {}", client.role());
					log.debug("Expected role: {}", UserRole.CLIENT.name());

					if (client.role() != UserRole.CLIENT) {
						return Mono.error(new UnauthorizedRequestException("User is not a client"));
					}

					return Mono.just(client);
				})
				.retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).maxBackoff(Duration.ofSeconds(5)))
				.doOnError(e -> log.error("Error fetching client {}: {}", clientId, e.getMessage()));
	}
}
