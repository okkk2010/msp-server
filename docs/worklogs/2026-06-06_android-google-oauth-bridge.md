# Task summary

Added the Android Google OAuth bridge so Android clients can start login without an existing JWT and receive tokens through the `msp-overlay://auth/callback` deep link.

# Scope

- Added Android OAuth session storage and callback URL validation.
- Added `GET /api/auth/android/google/start`.
- Allowed the Android OAuth start endpoint through Spring Security.
- Updated OAuth success and failure handlers to redirect Android clients back to the deep link callback.

# Changed files

- `src/main/java/com/mspoverlay/domain/auth/AuthController.java`
- `src/main/java/com/mspoverlay/global/security/SecurityConfig.java`
- `src/main/java/com/mspoverlay/infrastructure/oauth/AndroidOAuthLoginSession.java`
- `src/main/java/com/mspoverlay/infrastructure/oauth/OAuth2LoginFailureHandler.java`
- `src/main/java/com/mspoverlay/infrastructure/oauth/OAuth2LoginSuccessHandler.java`
- `docs/worklogs/_index.md`
- `docs/worklogs/2026-06-06_android-google-oauth-bridge.md`

# Verification result

- Ran `./gradlew.bat compileJava`.
- Result: `BUILD SUCCESSFUL`.
- Attempted `./gradlew.bat test --tests com.mspoverlay.domain.game.GameControllerTest`.
- Test execution failed before assertions because Mockito/ByteBuddy could not self-attach to the current JVM.

# Decisions made

- Android callback URLs are restricted to `msp-overlay://auth/callback`.
- Android success redirects include access token, refresh token, token type, refresh token expiry, and state.
- Android failure redirects include `error=oauth_login_failed` and state.

# Issues

- The hosted server must be redeployed before Android login can use the new endpoint.
- Existing test runtime needs a working Mockito inline mock maker environment to run controller tests.

# Next steps

- Deploy the server changes to `api.msp-overlay.store`.
- Verify `GET /api/auth/android/google/start?callbackUrl=msp-overlay%3A%2F%2Fauth%2Fcallback&state=test` redirects to Google OAuth.
- Re-test Android Google login after deployment.
