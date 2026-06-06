# Add Overlay Anchor Schema

## Task summary
Allow rectangle and circle overlay JSON to carry anchor metadata for edge-aware rendering on clients.

## Scope
- JSON Schema only.
- Rectangle and circle elements only.

## Changed files
- `src/main/resources/jsonschema/overlay-schema.json`

## Verification result
- JSON schema parsed successfully with Node.
- `./gradlew.bat compileJava` passed.

## Decisions made
- Added shared `anchor` enum values for top, center, bottom, left, and right combinations.
- Added `anchorSpace` enum values: `safeFrame` and `screen`.
- Anchor fields are optional for backward compatibility.

## Issues
- None found in this scope.

## Next steps
- Existing overlays without anchor metadata continue to rely on client defaults.
