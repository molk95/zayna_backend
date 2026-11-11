package com.zayna.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        String issuer,
        long accessTokenExpirationMinutes,
        long refreshTokenExpirationDays
) {
}