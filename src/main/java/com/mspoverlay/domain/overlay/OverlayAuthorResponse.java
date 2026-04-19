package com.mspoverlay.domain.overlay;

import com.mspoverlay.domain.user.User;

public record OverlayAuthorResponse(
        Long id,
        String name,
        String email
) {

    public static OverlayAuthorResponse from(User user) {
        return new OverlayAuthorResponse(user.getId(), user.getName(), user.getEmail());
    }
}
