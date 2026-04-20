package com.mspoverlay.global.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;
import com.mspoverlay.global.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = createSigningKey(jwtProperties.secret());
    }

    public String createAccessToken(AuthenticatedUser authenticatedUser) {
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(jwtProperties.accessTokenExpirationMinutes());
        return Jwts.builder()
                .subject(String.valueOf(authenticatedUser.userId()))
                .claim("email", authenticatedUser.email())
                .claim("name", authenticatedUser.name())
                .issuedAt(new Date())
                .expiration(Date.from(expiresAt.toInstant()))
                .signWith(signingKey)
                .compact();
    }

    public AuthenticatedUser getAuthenticatedUser(String accessToken) {
        Claims claims = parseClaims(accessToken);
        return new AuthenticatedUser(
                Long.parseLong(claims.getSubject()),
                claims.get("email", String.class),
                claims.get("name", String.class)
        );
    }

    public OffsetDateTime getRefreshTokenExpiry() {
        return OffsetDateTime.now().plusDays(jwtProperties.refreshTokenExpirationDays());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey createSigningKey(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret must not be blank");
        }
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (RuntimeException exception) {
            byte[] rawSecret = secret.getBytes(StandardCharsets.UTF_8);
            if (rawSecret.length >= 32) {
                return Keys.hmacShaKeyFor(rawSecret);
            }
            return Keys.hmacShaKeyFor(sha256(rawSecret));
        }
    }

    private byte[] sha256(byte[] value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is not available", exception);
        }
    }
}
