package com.precious.TaskApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

    @Value("${service.user-api}")
    private String userApiBaseUrl;

    @Value("${service.label-api}")
    private String labelApiBaseUrl;

    @Bean(name = "userApiClient")
    public WebClient userApiClient() {
        return WebClient.builder()
            .baseUrl(userApiBaseUrl)
            .build();
    }

    @Primary
    @Bean(name = "labelApiClient")
    public WebClient labelApiClient() {
        return WebClient.builder()
            .baseUrl(labelApiBaseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

}

