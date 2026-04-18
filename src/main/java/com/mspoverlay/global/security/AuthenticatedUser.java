package com.mspoverlay.global.security;

public record AuthenticatedUser(
        Long userId,
        String email,
        String name
) {
}
