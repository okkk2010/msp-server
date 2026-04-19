package com.mspoverlay.domain.overlay;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OverlayDetailResponse(
        Long id,
        String overlayId,
        String code,
        String name,
        String description,
        String platform,
        String game,
        String schemaVersion,
        Integer canvasBaseWidth,
        Integer canvasBaseHeight,
        BigDecimal opacity,
        String jsonPath,
        String thumbnailPath,
        OverlayAuthorResponse author,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static OverlayDetailResponse from(Overlay overlay) {
        return new OverlayDetailResponse(
                overlay.getId(),
                overlay.getOverlayId(),
                overlay.getCode(),
                overlay.getName(),
                overlay.getDescription(),
                overlay.getPlatform().getSlug(),
                overlay.getGame() != null ? overlay.getGame().getSlug() : null,
                overlay.getSchemaVersion(),
                overlay.getCanvasBaseWidth(),
                overlay.getCanvasBaseHeight(),
                overlay.getOpacity(),
                overlay.getJsonPath(),
                overlay.getThumbnailPath(),
                OverlayAuthorResponse.from(overlay.getAuthorUser()),
                overlay.getCreatedAt(),
                overlay.getUpdatedAt()
        );
    }
}
