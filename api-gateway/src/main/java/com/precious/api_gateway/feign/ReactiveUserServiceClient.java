package com.precious.api_gateway.feign;

import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import com.precious.api_gateway.dto.UserValidationResponse;
import reactor.core.publisher.Mono;

@Component
public class ReactiveUserServiceClient {
    private final UserServiceClient userServiceClient;

    public ReactiveUserServiceClient(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    public Mono<ResponseEntity<UserValidationResponse>> validateToken(String token) {
        return Mono.fromCallable(() -> userServiceClient.validateToken(token))
                .onErrorMap(e -> {
                    System.err.println("Error calling validate token: " + e.getMessage());
                    return e;
                });
    }
}
