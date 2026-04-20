package com.mspoverlay.global.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import com.mspoverlay.global.config.JwtProperties;

class JwtTokenProviderTest {

    @Test
    void createsAndParsesTokenWithShortPlaintextSecret() {
        JwtTokenProvider provider = new JwtTokenProvider(new JwtProperties("short-secret", 30, 14));
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(1L, "user@example.com", "User");

        String token = provider.createAccessToken(authenticatedUser);
        AuthenticatedUser parsed = provider.getAuthenticatedUser(token);

        assertThat(token).isNotBlank();
        assertThat(parsed).isEqualTo(authenticatedUser);
    }

    @Test
    void createsAndParsesTokenWithLongPlaintextSecret() {
        JwtTokenProvider provider = new JwtTokenProvider(
                new JwtProperties("local-dev-jwt-secret-local-dev-jwt-secret-2026", 30, 14)
        );
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(2L, "admin@example.com", "Admin");

        String token = provider.createAccessToken(authenticatedUser);
        AuthenticatedUser parsed = provider.getAuthenticatedUser(token);

        assertThat(parsed).isEqualTo(authenticatedUser);
    }
}
