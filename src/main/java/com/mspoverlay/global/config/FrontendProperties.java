package com.mspoverlay.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.frontend")
public record FrontendProperties(
        String origin,
        String loginCallbackPath
) {
}
