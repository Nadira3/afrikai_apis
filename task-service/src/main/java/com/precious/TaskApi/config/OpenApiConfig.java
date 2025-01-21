package com.precious.TaskApi.config;

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
                        .title("Task Service Documentation")
                        .version("1.0")
                        .description("Microservice that handles task creation, processing, assignment and monitoring"))
                
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
                .addTagsItem(new Tag().name("General").description("Endpoints related to general task management"))
                .addTagsItem(new Tag().name("Admin").description("Endpoints related to admin task management"))
                .addTagsItem(new Tag().name("Client").description("Endpoints related to client task management"))
                .addTagsItem(new Tag().name("User").description("Endpoints related to worker management"))
                .addTagsItem(new Tag().name("Other").description("Other general endpoints"));
    }
}
