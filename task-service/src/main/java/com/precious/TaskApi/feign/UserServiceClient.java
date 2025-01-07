package com.precious.TaskApi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

import com.precious.TaskApi.dto.UserValidationResponse;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {
    
    @GetMapping("/api/auth/validate")
    ResponseEntity<UserValidationResponse> validateToken(HttpServletRequest request);
}
