package com.mspoverlay.domain.auth;

import java.net.URI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mspoverlay.global.response.ApiResponse;
import com.mspoverlay.global.security.AuthenticatedUser;
import com.mspoverlay.infrastructure.oauth.AndroidOAuthLoginSession;
import com.mspoverlay.infrastructure.oauth.WindowsOAuthLoginSession;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

@Validated
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication and token APIs")
public class AuthController {

    private final AuthService authService;
    private final WindowsOAuthLoginSession windowsOAuthLoginSession;
    private final AndroidOAuthLoginSession androidOAuthLoginSession;

    public AuthController(
            AuthService authService,
            WindowsOAuthLoginSession windowsOAuthLoginSession,
            AndroidOAuthLoginSession androidOAuthLoginSession
    ) {
        this.authService = authService;
        this.windowsOAuthLoginSession = windowsOAuthLoginSession;
        this.androidOAuthLoginSession = androidOAuthLoginSession;
    }

    @GetMapping("/google")
    @Operation(summary = "Start Google OAuth login")
    public ResponseEntity<Void> googleLogin() {
        return ResponseEntity.status(302)
                .location(URI.create("/oauth2/authorization/google"))
                .build();
    }

    @GetMapping("/windows/google/start")
    @Operation(summary = "Start Google OAuth login for Windows client")
    public ResponseEntity<Void> windowsGoogleLogin(
            @RequestParam String callbackUrl,
            @RequestParam String state,
            HttpServletRequest request
    ) {
        if (!windowsOAuthLoginSession.isAllowedCallbackUrl(callbackUrl) || state == null || state.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        windowsOAuthLoginSession.save(request, callbackUrl, state);
        return ResponseEntity.status(302)
                .location(URI.create("/oauth2/authorization/google"))
                .build();
    }

    @GetMapping("/android/google/start")
    @Operation(summary = "Start Google OAuth login for Android client")
    public ResponseEntity<Void> androidGoogleLogin(
            @RequestParam String callbackUrl,
            @RequestParam String state,
            HttpServletRequest request
    ) {
        if (!androidOAuthLoginSession.isAllowedCallbackUrl(callbackUrl) || state == null || state.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        androidOAuthLoginSession.save(request, callbackUrl, state);
        return ResponseEntity.status(302)
                .location(URI.create("/oauth2/authorization/google"))
                .build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user")
    public ApiResponse<MeResponse> me(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ApiResponse.ok(authService.getCurrentUser(authenticatedUser.userId()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token with refresh token")
    public ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout current user and delete refresh tokens")
    public ApiResponse<Void> logout(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        authService.logout(authenticatedUser.userId());
        return ApiResponse.ok();
    }
}
