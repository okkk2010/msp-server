package com.mspoverlay.infrastructure.oauth;

import java.net.URI;
import java.util.Optional;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class AndroidOAuthLoginSession {

    private static final String CALLBACK_URL_ATTRIBUTE = "android_oauth_callback_url";
    private static final String STATE_ATTRIBUTE = "android_oauth_state";

    public void save(HttpServletRequest request, String callbackUrl, String state) {
        HttpSession session = request.getSession(true);
        session.setAttribute(CALLBACK_URL_ATTRIBUTE, callbackUrl);
        session.setAttribute(STATE_ATTRIBUTE, state);
    }

    public Optional<AndroidOAuthLoginRequest> consume(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }

        Object callbackUrl = session.getAttribute(CALLBACK_URL_ATTRIBUTE);
        Object state = session.getAttribute(STATE_ATTRIBUTE);
        session.removeAttribute(CALLBACK_URL_ATTRIBUTE);
        session.removeAttribute(STATE_ATTRIBUTE);

        if (!(callbackUrl instanceof String callbackUrlText) || callbackUrlText.isBlank()) {
            return Optional.empty();
        }

        if (!(state instanceof String stateText) || stateText.isBlank()) {
            return Optional.empty();
        }

        return Optional.of(new AndroidOAuthLoginRequest(callbackUrlText, stateText));
    }

    public boolean isAllowedCallbackUrl(String callbackUrl) {
        if (callbackUrl == null || callbackUrl.isBlank()) {
            return false;
        }

        try {
            URI uri = URI.create(callbackUrl);
            return "msp-overlay".equalsIgnoreCase(uri.getScheme())
                    && "auth".equalsIgnoreCase(uri.getHost())
                    && "/callback".equals(uri.getPath());
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public record AndroidOAuthLoginRequest(String callbackUrl, String state) {
    }
}
