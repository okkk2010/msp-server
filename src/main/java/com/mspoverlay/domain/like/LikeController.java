package com.mspoverlay.domain.like;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mspoverlay.global.response.ApiResponse;
import com.mspoverlay.global.security.AuthenticatedUser;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/likes")
@Tag(name = "Like", description = "Overlay like APIs")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    @Operation(summary = "Like an overlay")
    public ApiResponse<Void> like(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody LikeRequest request
    ) {
        likeService.like(authenticatedUser.userId(), request.overlayId());
        return ApiResponse.ok();
    }

    @DeleteMapping("/{overlayId}")
    @Operation(summary = "Remove like from an overlay")
    public ApiResponse<Void> unlike(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long overlayId
    ) {
        likeService.unlike(authenticatedUser.userId(), overlayId);
        return ApiResponse.ok();
    }
}
