package com.mspoverlay.domain.library;

import java.time.OffsetDateTime;
import com.mspoverlay.domain.overlay.OverlaySummaryResponse;

public record LibraryItemResponse(
        Long libraryId,
        OffsetDateTime savedAt,
        OverlaySummaryResponse overlay
) {

    public static LibraryItemResponse from(UserLibrary userLibrary) {
        return new LibraryItemResponse(
                userLibrary.getId(),
                userLibrary.getCreatedAt(),
                OverlaySummaryResponse.from(userLibrary.getOverlay())
        );
    }
}
