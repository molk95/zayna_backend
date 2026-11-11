package com.zayna.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(
        String email,
        String username,
        String password,
        String firstName,
        String lastName
) {
}