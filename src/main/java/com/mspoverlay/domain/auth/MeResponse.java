package com.mspoverlay.domain.auth;

import com.mspoverlay.domain.user.User;

public record MeResponse(
        Long id,
        String oauthProvider,
        String email,
        String name,
        String profileImageUrl
) {

    public static MeResponse from(User user) {
        return new MeResponse(
                user.getId(),
                user.getOauthProvider().name(),
                user.getEmail(),
                user.getName(),
                user.getProfileImageUrl()
        );
    }
}
