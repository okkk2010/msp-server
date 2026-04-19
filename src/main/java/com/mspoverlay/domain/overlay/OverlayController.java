package com.mspoverlay.domain.overlay;

import com.mspoverlay.global.response.ApiResponse;
import com.mspoverlay.global.response.PageResponse;
import com.mspoverlay.global.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/overlays")
public class OverlayController {

    private final OverlayUploadService overlayUploadService;
    private final OverlayQueryService overlayQueryService;
    private final OverlayCommandService overlayCommandService;

    public OverlayController(
            OverlayUploadService overlayUploadService,
            OverlayQueryService overlayQueryService,
            OverlayCommandService overlayCommandService
    ) {
        this.overlayUploadService = overlayUploadService;
        this.overlayQueryService = overlayQueryService;
        this.overlayCommandService = overlayCommandService;
    }

    @GetMapping
    public ApiResponse<PageResponse<OverlaySummaryResponse>> getOverlays(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String game,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.ok(overlayQueryService.getOverlays(page, size, keyword, platform, game, code, sort));
    }

    @GetMapping("/{overlayId}")
    public ApiResponse<OverlayDetailResponse> getOverlayDetail(@PathVariable String overlayId) {
        return ApiResponse.ok(overlayQueryService.getOverlayDetail(overlayId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<OverlayUploadResponse>> upload(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @ModelAttribute OverlayUploadRequest request
    ) {
        OverlayUploadResponse response = overlayUploadService.upload(authenticatedUser.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PatchMapping(value = "/{overlayId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<OverlayUploadResponse> update(
            @PathVariable String overlayId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @ModelAttribute OverlayUpdateRequest request
    ) {
        return ApiResponse.ok(overlayCommandService.update(overlayId, authenticatedUser.userId(), request));
    }

    @DeleteMapping("/{overlayId}")
    public ApiResponse<Void> delete(
            @PathVariable String overlayId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        overlayCommandService.delete(overlayId, authenticatedUser.userId());
        return ApiResponse.ok();
    }
}
