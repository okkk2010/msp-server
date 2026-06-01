# Allow Localhost Cors Patterns

## Task summary
- Added localhost CORS origin pattern support for local frontend development.

## Scope
- Added `app.cors.allowed-origin-patterns` configuration.
- Applied allowed origin patterns in Spring Security CORS configuration.
- Added the Docker Compose environment variable default for localhost patterns.

## Changed files
- `src/main/resources/application.yml`
- `src/main/java/com/mspoverlay/global/config/CorsProperties.java`
- `src/main/java/com/mspoverlay/global/security/SecurityConfig.java`
- `docker-compose.yml`
- `docs/worklogs/_index.md`
- `docs/worklogs/2026-06-01_allow-localhost-cors-patterns.md`

## Verification result
- `./gradlew.bat classes` passed.
- `./gradlew.bat test` ran, but 1 existing test failed:
  - `com.mspoverlay.domain.overlay.OverlayControllerTest`
  - Failure: missing JSON path `$.data.overlayJson.overlayId`

## Decisions made
- Kept exact `CORS_ALLOWED_ORIGINS` support.
- Added `CORS_ALLOWED_ORIGIN_PATTERNS` for values like `http://localhost:*` and `http://127.0.0.1:*`.
- Split comma-separated CORS environment values in code so Docker/env strings work as multiple values.

## Issues
- The failing overlay controller test appears unrelated to CORS configuration, but should be reviewed separately.

## Next steps
- Restart the backend server or Docker Compose app container so the new CORS settings are applied.
