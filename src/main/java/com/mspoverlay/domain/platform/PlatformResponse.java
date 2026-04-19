package com.mspoverlay.domain.platform;

public record PlatformResponse(
        Long id,
        String name,
        String slug
) {

    public static PlatformResponse from(Platform platform) {
        return new PlatformResponse(platform.getId(), platform.getName(), platform.getSlug());
    }
}
