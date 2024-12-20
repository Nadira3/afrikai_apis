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
	// Inject task and client service URLs
        @Value("${services.task-service.url}") String taskServiceUrl,
        @Value("${services.client-service.url}") String clientServiceUrl
    ) {
	/**
		* This creates an instance of web client for the task service
		* and client service
		* Initialize WebClient with retry and timeout configurations
	*/
        this.taskServiceClient = WebClient.builder()
		/**
		 * Set base URL; this will be prepended to all requests
			* For example, if the base URL is "http://localhost:8080",
			* and a request is made to "/api/v1/tasks/123",
			* the full URL will be "http://localhost:8080/api/v1/tasks/123"
		*/
            .baseUrl(taskServiceUrl)
	    		/**
			 * Set default headers for all requests
			 * In this case, set the content type to JSON
			 * This will be included in all requests made by this client
			 * This can be overridden in individual requests
			 * For example, if a request is made with a different content type,
			 * that content type will be used instead of the default
			 * This is useful for setting common headers that are used across all requests
			 * For example, setting the content type to JSON for a REST API
			 * This ensures that all requests made by this client are JSON requests
			 */
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	    		/**
			 * Add a filter to the WebClient
			 * Filters are used to modify requests and responses
			 * In this case, add basic authentication to all requests
			 * This will add an Authorization header with the username and password
			 * This is useful for APIs that require authentication
			 * This can be overridden in individual requests if needed
			 * For example, if a request is made to a public endpoint that does not require authentication,
			 * the filter can be removed for that request
			 */
            .filter(ExchangeFilterFunctions.basicAuthentication("username", "password"))
	    		/**
			 * Build the WebClient
			 * This creates an instance of the WebClient with the specified configurations
			 * The WebClient is now ready to make requests to the specified base URL
			 */
            .build();
        
	// Create a WebClient for the client service. Same logic as above
        this.clientServiceClient = WebClient.builder()
            .baseUrl(clientServiceUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(ExchangeFilterFunctions.basicAuthentication("username", "password"))
            .build();

        /**
	 * Configure circuit breaker
	 * Circuit breaker is used to handle failures in external services
	 * It prevents cascading failures and provides fallback behavior
	 *
		* This line creates a cicuit breaker named "external-service"
		* with the following configuration:
		* - Failure rate threshold: 50%
		*   If the failure rate exceeds 50%, the circuit breaker will open
		*   and prevent further requests to the external service
		*   This helps prevent overloading the external service
		*   and allows it to recover from failures
		*   The failure rate is calculated based on the number of failed requests
		*   over a sliding window of 2 requests
		*
		*   - Wait duration in open state: 1000 milliseconds
		*   If the circuit breaker is open, it will remain open for 1000 milliseconds
		*   This is the time period during which requests will be blocked
		*   After the wait duration, the circuit breaker will enter a half-open state
		*   and allow a single request to pass through
		*   If the request succeeds, the circuit breaker will close
		*
		*   - Sliding window size: 2
		*   The sliding window size is the number of requests over which the failure rate is calculated
		*   In this case, the failure rate is calculated over the last 2 requests
		*   This allows the circuit breaker to respond quickly to changes in the failure rate
		*   and adapt to the current state of the external service
		*   The sliding window size can be adjusted based on the requirements of the system
		*
		*   The circuit breaker is created using the CircuitBreaker.of() method
		*   with the specified configuration
	     */
        this.circuitBreaker = CircuitBreaker.of("external-service", CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofMillis(1000))
            .slidingWindowSize(2)
            .build());
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
	      * In this case, the supplier calls the task service to fetch the task reference DTO
	      * The supplier is executed when the Mono is subscribed to
	      * This allows the task service call to be made asynchronously
	      * The result of the task service call is wrapped in a Mono
	      */
        return Mono.fromSupplier(() -> 
            circuitBreaker.executeSupplier(() -> 
                taskServiceClient.get()
                    .uri("/api/v1/tasks/{id}", taskId)
                    .retrieve() // Retrieve the response
		    /**
		      * Handle specific HTTP status codes returned by the external service
		      * In this case, handle 4xx client errors and 5xx server errors
		      *
		      * 4xx client errors indicate that the request was invalid or
		      * the resource was not found (400 or 404)
		      *
		      * 5xx server errors indicate that the external service encountered an error(500) 
		      * and was unable to process the request
		      */
                    .onStatus(
                        HttpStatus::is4xxClientError,
                        response -> Mono.error(new ExternalServiceException("Task Service", "Task not found: " + taskId))
                    )
                    .onStatus(
                        HttpStatus::is5xxServerError,
                        response -> Mono.error(new ExternalServiceException("Task Service", "Server error"))
                    )
		    // Convert the response body to a TaskReferenceDto
                    .bodyToMono(TaskReferenceDto.class)
		    /**
		     * Retry the request in case of transient failures
		     *
		     * Blocking means that the current thread will wait for the result
		     * This is done to ensure that the task reference DTO is available
		     * before proceeding with the next step
		     * In this case, the task reference DTO is required to fetch the client information
		     */
                    .block()
            )
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
     * The method fetches client information from the client service
     * Same logic as getTaskReference method
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
