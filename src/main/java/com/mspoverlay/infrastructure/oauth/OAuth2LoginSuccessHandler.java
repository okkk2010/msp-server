package com.mspoverlay.infrastructure.oauth;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.mspoverlay.domain.auth.AuthService;
import com.mspoverlay.domain.auth.AuthTokenResponse;
import com.mspoverlay.domain.user.OAuthProvider;
import com.mspoverlay.global.config.FrontendProperties;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final FrontendProperties frontendProperties;

    public OAuth2LoginSuccessHandler(AuthService authService, FrontendProperties frontendProperties) {
        this.authService = authService;
        this.frontendProperties = frontendProperties;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();

        String registrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        OAuthProvider provider = OAuthProvider.valueOf(registrationId.toUpperCase());

        String providerUserId = attribute(oAuth2User, "sub");
        String email = oAuth2User.getAttribute("email");
        String name = attribute(oAuth2User, "name");
        String picture = oAuth2User.getAttribute("picture");

        AuthTokenResponse authTokenResponse = authService.login(provider, providerUserId, email, name, picture);

        response.sendRedirect(loginCallbackUri()
                .queryParam("success", true)
                .queryParam("accessToken", authTokenResponse.accessToken())
                .queryParam("refreshToken", authTokenResponse.refreshToken())
                .queryParam("tokenType", authTokenResponse.tokenType())
                .queryParam("refreshTokenExpiresAt", authTokenResponse.refreshTokenExpiresAt())
                .build(true)
                .toUriString());
    }

    private String attribute(OAuth2User oAuth2User, String key) {
        String value = oAuth2User.getAttribute(key);
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "OAuth 사용자 정보가 올바르지 않습니다.");
        }
        return value;
    }

    private UriComponentsBuilder loginCallbackUri() {
        URI origin = URI.create(frontendProperties.origin());
        String callbackPath = frontendProperties.loginCallbackPath();
        return UriComponentsBuilder.fromUri(origin)
                .path(callbackPath)
                .encode(StandardCharsets.UTF_8);
    }
}
