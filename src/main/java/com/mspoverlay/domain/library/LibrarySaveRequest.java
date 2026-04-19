package com.mspoverlay.domain.library;

import jakarta.validation.constraints.NotNull;

public record LibrarySaveRequest(
        @NotNull(message = "overlayId는 필수입니다.")
        Long overlayId
) {
}
