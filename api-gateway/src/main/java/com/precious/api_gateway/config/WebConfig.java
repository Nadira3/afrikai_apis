package com.precious.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;

@Configuration
public class WebConfig {

    /**
     * This is the default HttpMessageConverters that Spring Boot provides.
     * It functions to convert the request and response body to and from Java objects.
     *
     * @return HttpMessageConverters
     */
    @Bean
    public HttpMessageConverters httpMessageConverters() {
        return new HttpMessageConverters();  // Default converters
    }
}
