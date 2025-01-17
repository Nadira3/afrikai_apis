package com.precious.api_gateway.feign;

import com.precious.api_gateway.dto.UserValidationResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "USER-SERVICE", url = "http://localhost:8081")
public interface UserServiceClient {

    @PostMapping("/api/auth/validate")
    ResponseEntity<UserValidationResponse> validateToken(
        @RequestHeader("Authorization") String token);
}
