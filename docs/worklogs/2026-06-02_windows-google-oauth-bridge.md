# Task Summary

Added a Windows-client Google OAuth bridge so the Windows app can receive OAuth results through its localhost callback.

# Scope

Implemented the backend endpoint and redirect handling required by the Windows client login flow. Existing frontend OAuth behavior is preserved.

# Changed Files

- `src/main/java/com/mspoverlay/domain/auth/AuthController.java`
- `src/main/java/com/mspoverlay/global/security/SecurityConfig.java`
- `src/main/java/com/mspoverlay/infrastructure/oauth/OAuth2LoginSuccessHandler.java`
- `src/main/java/com/mspoverlay/infrastructure/oauth/OAuth2LoginFailureHandler.java`
- `src/main/java/com/mspoverlay/infrastructure/oauth/WindowsOAuthLoginSession.java`
- `docs/worklogs/_index.md`
- `docs/worklogs/2026-06-02_windows-google-oauth-bridge.md`

# Verification Result

- `./gradlew.bat compileJava` succeeded.
- `./gradlew.bat test` ran after allowing network access, but 2 tests failed because Testcontainers could not find a Docker environment. The failures were unrelated to this OAuth change.

# Decisions Made

- Added `GET /api/auth/windows/google/start?callbackUrl=...&state=...`.
- Stored the Windows callback URL and app state in the OAuth HTTP session before redirecting to `/oauth2/authorization/google`.
- On OAuth success, redirects to the Windows callback with `accessToken` and the original Windows state.
- On OAuth failure, redirects to the Windows callback with `error=oauth_login_failed` and the original Windows state.
- Allowed only localhost callback URLs on `/auth/callback` or `/auth/callback/`.

# Issues

- Full test execution still requires a working Docker/Testcontainers environment.

# Next Steps

- Run the Windows app login against the server and confirm the browser returns to `http://127.0.0.1:51321/auth/callback/`.
