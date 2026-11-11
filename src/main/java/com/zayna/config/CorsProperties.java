package com.zayna.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(List<String> allowedOriginPatterns) {

    public CorsProperties {
        if (allowedOriginPatterns == null) {
            allowedOriginPatterns = List.of();
        }
    }
}