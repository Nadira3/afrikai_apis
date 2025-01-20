package com.precious.api_gateway.security;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.precious.api_gateway.dto.UserValidationResponse;
import com.precious.api_gateway.feign.ReactiveUserServiceClient;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {
    
    // Logger; used to log messages to the console
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Feign client to communicate with the user service reactively
    private final ReactiveUserServiceClient userServiceClient;

    // Constructor for injecting the user service client
    public JwtAuthenticationFilter(ReactiveUserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
	// Extract the Authorization header from the request
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

	// If the header is not present or does not start with "Bearer ", continue with the request
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

	// Extract the token from the header
        String token = authHeader.substring(7);

	// Validate the token using the user service client
        return userServiceClient.validateToken(token)
            .flatMap(response -> {
		/**
		 * If the response is successful and the user details are valid,
		 * create a new authentication token with the user details and
		 * inject it into the request. Then, continue with the request.
		 */
                UserValidationResponse userDetails = response.getBody();
                if (userDetails != null && userDetails.isValid()) {
                    List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + userDetails.getRole())
                    );

		    // Create a custom user details object with the user details
                    CustomUserDetails customUserDetails = new CustomUserDetails(
                        userDetails.getUserId(),
                        userDetails.getRole(),
                        authorities
                    );

		    // Create an authentication token with the custom user details
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            customUserDetails,
                            null,
                            authorities
                        );

		    // Inject the user ID and role into the request headers
		    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
			.header("X-User-Id", userDetails.getUserId().toString())
			.header("X-User-Role", userDetails.getRole())
			.build();

                    // Mutate the exchange with the mutated request
		    ServerWebExchange mutatedExchange = exchange.mutate()
			    .request(mutatedRequest)
			    .build();

		    // Continue with the request and inject the authentication token
		    return chain.filter(mutatedExchange)
			    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

                }
		/**
		 * If the response is successful but the user details are invalid,
		 * log an error message and continue with the request.
		 * This will result in a 401 Unauthorized response.
		 */
                return chain.filter(exchange);
            })
	    // If the response is not successful, log an error message and continue with the request
            .onErrorResume(e -> {
                log.error("JWT validation failed: {}", e.getMessage());
                return chain.filter(exchange);
            });
    }
}
