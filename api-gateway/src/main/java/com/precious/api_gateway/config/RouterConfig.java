package com.precious.api_gateway.config;

import com.precious.api_gateway.dto.UserValidationResponse;
import com.precious.api_gateway.feign.UserServiceClient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/auth/validate",
            operation = @Operation(
                operationId = "validateToken",
                summary = "Validate User Token",
                description = "Validates the JWT token by communicating with the User Service",
                security = @SecurityRequirement(name = "bearer-token"),
                parameters = {
                    @Parameter(
                        in = ParameterIn.HEADER,
                        name = "Authorization",
                        description = "JWT token with Bearer prefix",
                        required = true,
                        schema = @Schema(type = "string")
                    )
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Token successfully validated",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserValidationResponse.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "401",
                        description = "Invalid or expired token"
                    )
                }
            )
        )
    })
    public RouterFunction<ServerResponse> validateTokenRoute(UserServiceClient userServiceClient) {
        return route(POST("/api/auth/validate"), 
            request -> Mono.just(request)
                .map(req -> req.headers().asHttpHeaders().getFirst("Authorization"))
                .map(token -> userServiceClient.validateToken(token))
                .flatMap(response -> ServerResponse.ok().bodyValue(response)));
    }
}
