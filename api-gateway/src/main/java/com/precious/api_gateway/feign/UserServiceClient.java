package com.precious.api_gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;

import com.precious.api_gateway.dto.UserValidationResponse;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {
    
    @PostMapping("/api/auth/validate")
    ResponseEntity<UserValidationResponse> validateToken(
        @RequestHeader("Authorization") String token);
}
