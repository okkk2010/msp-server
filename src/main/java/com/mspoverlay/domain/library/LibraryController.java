package com.mspoverlay.domain.library;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public ApiResponse<List<LibraryItemResponse>> getLibrary(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ApiResponse.ok(libraryService.getLibrary(authenticatedUser.userId()));
    }

    @PostMapping
    public ApiResponse<Void> save(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody LibrarySaveRequest request
    ) {
        libraryService.save(authenticatedUser.userId(), request.overlayId());
        return ApiResponse.ok();
    }

    @DeleteMapping("/{overlayId}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long overlayId
    ) {
        libraryService.delete(authenticatedUser.userId(), overlayId);
        return ApiResponse.ok();
    }
}
