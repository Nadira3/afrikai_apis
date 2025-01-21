package com.precious.UserApi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Define basic info for your API documentation
                .info(new Info()
                        .title("UserService Documentation")
                        .version("1.0")
                        .description("Microservice that handles user registration, authentication, and management"))
                
                // Define security scheme (Authorization header)
                .addSecurityItem(new SecurityRequirement().addList("bearer-token"))
                .components(new Components()
                        .addSecuritySchemes("bearer-token",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ))

                // Define tags for grouping endpoints
                .addTagsItem(new Tag().name("User Management").description("Endpoints related to user registration and management"))
                .addTagsItem(new Tag().name("Authentication").description("Endpoints related to user authentication"))
                .addTagsItem(new Tag().name("Other").description("Other general endpoints"));
    }
}
