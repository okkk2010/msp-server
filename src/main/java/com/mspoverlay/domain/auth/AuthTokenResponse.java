package com.mspoverlay.domain.auth;

import java.time.OffsetDateTime;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        OffsetDateTime refreshTokenExpiresAt
) {

    public static AuthTokenResponse of(
            String accessToken,
            String refreshToken,
            OffsetDateTime refreshTokenExpiresAt
    ) {
        return new AuthTokenResponse(accessToken, refreshToken, "Bearer", refreshTokenExpiresAt);
    }
}
