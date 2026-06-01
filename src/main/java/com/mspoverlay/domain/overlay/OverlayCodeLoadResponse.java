package com.mspoverlay.domain.overlay;

import java.time.OffsetDateTime;

public record OverlayCodeLoadResponse(
        Long id,
        String overlayId,
        String code,
        String name,
        String description,
        String platform,
        OverlayCodeLoadPlatformResponse platformInfo,
        OverlayCodeLoadGameResponse game,
        String thumbnailUrl,
        String schemaVersion,
        String overlayJson,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static OverlayCodeLoadResponse from(Overlay overlay, String overlayJson) {
        return new OverlayCodeLoadResponse(
                overlay.getId(),
                overlay.getOverlayId(),
                overlay.getCode(),
                overlay.getName(),
                overlay.getDescription(),
                overlay.getPlatform().getSlug(),
                OverlayCodeLoadPlatformResponse.from(overlay),
                OverlayCodeLoadGameResponse.from(overlay),
                overlay.getThumbnailPath(),
                overlay.getSchemaVersion(),
                overlayJson,
                overlay.getCreatedAt(),
                overlay.getUpdatedAt()
        );
    }
}
