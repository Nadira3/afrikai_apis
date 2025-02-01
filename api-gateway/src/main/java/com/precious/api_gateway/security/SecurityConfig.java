package com.precious.api_gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

import com.precious.api_gateway.feign.ReactiveUserServiceClient;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    // Inject the user service client
    private final ReactiveUserServiceClient userServiceClient;

    public SecurityConfig(ReactiveUserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF
            .authorizeExchange(exchanges -> exchanges
		.pathMatchers(
			"",
			"/",
			"/templates/**",
			"/css/**",
			"/js/**",
			"/images/**",
			"/swagger-ui/**",
			"/swagger-ui.html",
			"/webjars/**",
			"/v3/api-docs/**",
			"/login",
			"/api/auth/**"
		).permitAll() // Allow access to the Swagger UI and the authentication endpoint
                .anyExchange().authenticated() // Require authentication for all other requests
            )
            .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }

    /**
     * @return a new instance of the JwtAuthenticationFilter
     * @see JwtAuthenticationFilter
     * @see ReactiveUserServiceClient
     * @see UserServiceClient
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(userServiceClient);
    }
}
