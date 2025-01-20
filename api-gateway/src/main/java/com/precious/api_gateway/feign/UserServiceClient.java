package com.precious.api_gateway.feign;

import com.precious.api_gateway.dto.UserValidationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Authentication", description = "User authentication and token validation endpoints")
@FeignClient(name = "USER-SERVICE", url = "http://localhost:8081")
public interface UserServiceClient {
    
    @Operation(
        summary = "Validate User Token",
        description = "Validates the JWT token by communicating with the User Service",
        security = @SecurityRequirement(name = "bearer-token"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Token successfully validated",
                content = @Content(schema = @Schema(implementation = UserValidationResponse.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Invalid or expired token"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error or User Service unavailable"
            )
        }
    )
    @PostMapping("/api/auth/validate")
    ResponseEntity<UserValidationResponse> validateToken(
        @Parameter(description = "JWT token with Bearer prefix", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIs...")
        @RequestHeader("Authorization") String token
    );
}
