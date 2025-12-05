package com.anipulse.animeservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.jikan")
@Data
public class JikanProperties {
    private String baseUrl;
    private Integer rateLimitDelay;
    private Integer retryAttempts;
    private Long cacheExpiration;
}
