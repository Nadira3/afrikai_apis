package com.precious.TaskApi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "storage")
@Data
public class StorageProperties {
    private String location = "upload-dir";
}