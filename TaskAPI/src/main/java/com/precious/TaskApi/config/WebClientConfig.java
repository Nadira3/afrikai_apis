package com.precious.TaskApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

    @Value("${task-specific-api.port}")
    private String port;

    @Bean
    public WebClient taskSpecificApiClient() {
        return WebClient.builder()
            .baseUrl("http://localhost:{port}")
            .build();
    }

}
