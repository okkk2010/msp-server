package com.mspoverlay.domain.overlay;

import java.time.OffsetDateTime;

public record OverlaySummaryResponse(
        Long id,
        String overlayId,
        String code,
        String name,
        String description,
        String platform,
        String game,
        String thumbnailPath,
        String authorName,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static OverlaySummaryResponse from(Overlay overlay) {
        return new OverlaySummaryResponse(
                overlay.getId(),
                overlay.getOverlayId(),
                overlay.getCode(),
                overlay.getName(),
                overlay.getDescription(),
                overlay.getPlatform().getSlug(),
                overlay.getGame() != null ? overlay.getGame().getSlug() : null,
                overlay.getThumbnailPath(),
                overlay.getAuthorUser().getName(),
                overlay.getCreatedAt(),
                overlay.getUpdatedAt()
        );
    }
}
