package com.mspoverlay.domain.overlay;

public record OverlayCodeLoadPlatformResponse(
        Long id,
        String name,
        String slug
) {

    public static OverlayCodeLoadPlatformResponse from(Overlay overlay) {
        return new OverlayCodeLoadPlatformResponse(
                overlay.getPlatform().getId(),
                overlay.getPlatform().getName(),
                overlay.getPlatform().getSlug()
        );
    }
}
