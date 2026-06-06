# Task summary

Added Android game category seed data for the `games` table.

# Scope

- Added a new Flyway migration for Android platform games.
- Kept the existing Windows seed migration unchanged.
- Used the existing `(platform_id, slug)` conflict handling pattern for idempotent updates.

# Changed files

- `src/main/resources/db/migration/V3__seed_android_games.sql`
- `docs/worklogs/_index.md`
- `docs/worklogs/2026-06-06_seed-android-games.md`

# Verification result

- Ran `./gradlew.bat test`.
- Full test run failed because Docker/Testcontainers was unavailable for `MspOverlayApplicationTests` and `OverlayUploadIntegrationTest`.
- Ran `./gradlew.bat test --tests com.mspoverlay.domain.game.GameControllerTest`.
- Targeted game controller test passed.

# Decisions made

- Seeded Android entries under the existing `android` platform row from `V1__init_schema.sql`.
- Included common Android/mobile games that are suitable for overlay categorization.
- Reused overlapping titles such as `minecraft` and `fortnite` with Android platform-specific rows, which is supported by the `(platform_id, slug)` uniqueness rule.

# Issues

- No live database migration was executed in this step.
- Full backend test verification requires a working Docker/Testcontainers environment.

# Next steps

- Run backend tests.
- Apply Flyway migrations to the target database.
- Verify `GET /api/games?platform=android` returns the seeded entries.
