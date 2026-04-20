package com.mspoverlay.domain.overlay;

import java.time.OffsetDateTime;
import com.fasterxml.jackson.databind.JsonNode;

public record OverlayCodeLoadResponse(
        Long id,
        String overlayId,
        String code,
        String name,
        String description,
        OverlayCodeLoadPlatformResponse platform,
        OverlayCodeLoadGameResponse game,
        String thumbnailUrl,
        String schemaVersion,
        JsonNode overlayJson,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static OverlayCodeLoadResponse from(Overlay overlay, JsonNode overlayJson) {
        return new OverlayCodeLoadResponse(
                overlay.getId(),
                overlay.getOverlayId(),
                overlay.getCode(),
                overlay.getName(),
                overlay.getDescription(),
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
