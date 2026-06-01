# Require Category On Overlay Upload

## Task summary
- Made category/game selection mandatory for overlay upload.
- Kept `game: null` invalid in overlay JSON.

## Scope
- Added server-side `gameId` validation for upload requests.
- Added `game` to the required overlay JSON schema fields.
- Updated upload integration tests to include category data and cover missing category rejection.

## Changed files
- `src/main/java/com/mspoverlay/domain/overlay/OverlayUploadRequest.java`
- `src/main/resources/jsonschema/overlay-schema.json`
- `src/test/java/com/mspoverlay/domain/overlay/OverlayUploadIntegrationTest.java`
- `docs/worklogs/_index.md`
- `docs/worklogs/2026-06-01_require-category-on-overlay-upload.md`

## Verification result
- `./gradlew.bat test --tests com.mspoverlay.domain.overlay.OverlayUploadIntegrationTest` passed.

## Decisions made
- The server rejects uploads without `gameId` before saving files or DB rows.
- Overlay JSON must include a non-null `game` object.

## Issues
- No new issues found.

## Next steps
- Keep frontend upload validation aligned so users cannot submit without selecting a category.
