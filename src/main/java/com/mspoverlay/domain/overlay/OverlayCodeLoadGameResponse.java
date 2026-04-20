package com.mspoverlay.domain.overlay;

public record OverlayCodeLoadGameResponse(
        Long id,
        String slug,
        String displayName
) {

    public static OverlayCodeLoadGameResponse from(Overlay overlay) {
        if (overlay.getGame() == null) {
            return null;
        }

        return new OverlayCodeLoadGameResponse(
                overlay.getGame().getId(),
                overlay.getGame().getSlug(),
                overlay.getGame().getDisplayName()
        );
    }
}
