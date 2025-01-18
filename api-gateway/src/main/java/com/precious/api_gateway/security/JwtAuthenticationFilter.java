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
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final ReactiveUserServiceClient userServiceClient;

    public JwtAuthenticationFilter(ReactiveUserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        return userServiceClient.validateToken(token)
            .flatMap(response -> {
                UserValidationResponse userDetails = response.getBody();
                if (userDetails != null && userDetails.isValid()) {
                    List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + userDetails.getRole())
                    );

                    CustomUserDetails customUserDetails = new CustomUserDetails(
                        userDetails.getUserId(),
                        userDetails.getRole(),
                        authorities
                    );

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            customUserDetails,
                            null,
                            authorities
                        );
		    
		    // Inject user details into the request
		    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
			.header("X-User-Id", userDetails.getUserId().toString())
			.header("X-User-Role", userDetails.getRole())
			.build();

                    // Mutate the exchange with the new request
		    ServerWebExchange mutatedExchange = exchange.mutate()
			    .request(mutatedRequest)
			    .build();

		    return chain.filter(mutatedExchange)
			    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

                }
                return chain.filter(exchange);
            })
            .onErrorResume(e -> {
                log.error("JWT validation failed: {}", e.getMessage());
                return chain.filter(exchange);
            });
    }
}
