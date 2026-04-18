package com.mspoverlay.domain.overlay;

public record OverlayUploadResponse(
        Long id,
        String overlayId,
        String code,
        String jsonPath,
        String thumbnailPath
) {
}
