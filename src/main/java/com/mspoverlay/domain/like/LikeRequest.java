package com.mspoverlay.domain.like;

import jakarta.validation.constraints.NotNull;

public record LikeRequest(
        @NotNull(message = "overlayId는 필수입니다.")
        Long overlayId
) {
}
