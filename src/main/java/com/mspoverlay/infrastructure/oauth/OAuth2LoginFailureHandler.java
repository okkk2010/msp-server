package com.mspoverlay.infrastructure.oauth;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import com.mspoverlay.global.config.FrontendProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private final FrontendProperties frontendProperties;

    public OAuth2LoginFailureHandler(FrontendProperties frontendProperties) {
        this.frontendProperties = frontendProperties;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        response.sendRedirect(loginCallbackUri()
                .queryParam("success", false)
                .queryParam("error", "oauth_login_failed")
                .queryParam("message", exception.getMessage())
                .build(true)
                .toUriString());
    }

    private UriComponentsBuilder loginCallbackUri() {
        URI origin = URI.create(frontendProperties.origin());
        String callbackPath = frontendProperties.loginCallbackPath();
        return UriComponentsBuilder.fromUri(origin)
                .path(callbackPath)
                .encode(StandardCharsets.UTF_8);
    }
}
