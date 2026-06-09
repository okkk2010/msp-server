package com.mspoverlay.domain.overlay;

import com.mspoverlay.global.response.ApiResponse;
import com.mspoverlay.global.response.PageResponse;
import com.mspoverlay.global.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Overlay", description = "Overlay upload, query, update and delete APIs")
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
    @Operation(summary = "Get paged overlay list")
    public ApiResponse<PageResponse<OverlaySummaryResponse>> getOverlays(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String game,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String sort
    ) {
        Long currentUserId = authenticatedUser != null ? authenticatedUser.userId() : null;
        return ApiResponse.ok(overlayQueryService.getOverlays(page, size, keyword, platform, game, code, sort, currentUserId));
    }

    @GetMapping("/{overlayId}")
    @Operation(summary = "Get overlay detail by overlayId")
    public ApiResponse<OverlayDetailResponse> getOverlayDetail(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable String overlayId
    ) {
        Long currentUserId = authenticatedUser != null ? authenticatedUser.userId() : null;
        return ApiResponse.ok(overlayQueryService.getOverlayDetail(overlayId, currentUserId));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get overlay by share code")
    public ApiResponse<OverlayCodeLoadResponse> getOverlayByCode(@PathVariable String code) {
        return ApiResponse.ok(overlayQueryService.getOverlayByCode(code));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload overlay files and metadata")
    public ResponseEntity<ApiResponse<OverlayUploadResponse>> upload(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @ModelAttribute OverlayUploadRequest request
    ) {
        OverlayUploadResponse response = overlayUploadService.upload(authenticatedUser.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PatchMapping(value = "/{overlayId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update overlay metadata or files")
    public ApiResponse<OverlayUploadResponse> update(
            @PathVariable String overlayId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @ModelAttribute OverlayUpdateRequest request
    ) {
        return ApiResponse.ok(overlayCommandService.update(overlayId, authenticatedUser.userId(), request));
    }

    @DeleteMapping("/{overlayId}")
    @Operation(summary = "Delete overlay and stored files")
    public ApiResponse<Void> delete(
            @PathVariable String overlayId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        overlayCommandService.delete(overlayId, authenticatedUser.userId());
        return ApiResponse.ok();
    }
}
