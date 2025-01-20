package com.precious.api_gateway.config;

import com.precious.api_gateway.feign.UserServiceClient;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.ServerRequest;

import reactor.core.publisher.Mono;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class OpenApiConfig {
   
    /**
     * OpenAPI configuration
     * @return OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-token", 
                            new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("API Gateway Documentation")
                        .version("1.0")
                        .description("API Gateway service that handles token validation through User Service"));
    }

    /**
     * Router function for token validation
     * @param userServiceClient
     * @return RouterFunction<ServerResponse>
     */
    @Bean
    public RouterFunction<ServerResponse> routerFunction(UserServiceClient userServiceClient) {
        return route(POST("/api/auth/validate"), request -> 
            Mono.justOrEmpty(request.headers().firstHeader("Authorization"))
                .flatMap(token -> Mono.just(userServiceClient.validateToken(token)))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .switchIfEmpty(ServerResponse.badRequest().build())
        );
    }
}
