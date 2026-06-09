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
        long likeCount,
        long savedCount,
        boolean likedByMe,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static OverlayDetailResponse from(Overlay overlay) {
        return from(overlay, false);
    }

    public static OverlayDetailResponse from(Overlay overlay, boolean likedByMe) {
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
                overlay.getLikeCount(),
                overlay.getSaveCount(),
                likedByMe,
                overlay.getCreatedAt(),
                overlay.getUpdatedAt()
        );
    }
}
