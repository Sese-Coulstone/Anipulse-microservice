package com.anipulse.animeservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "external-api.jikan")
public class ExternalApiConfig {
    @Value("${app.jikan.base-url}")
    private String baseUrl;

    @Value("${app.jikan.timeout}")
    private int timeout;

    @Value("${app.jikan.retryAttempts}")
    private int retryAttempts;

    @Value("${app.jikan.rateLimitDelay}")
    private int rateLimitDelay;
}
