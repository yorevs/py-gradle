# Release Checklist

## Pre-Release
- [ ] Update version (`patchVersion`, `updateMinor`, or `updateMajor`).
- [ ] Update changelog (`changelog`).
- [ ] Run tests (`./gradlew ciTest`).
- [ ] Validate plugin (`./gradlew validatePlugins`).

## Publish Plugin
- [ ] Export Plugin Portal credentials:
  - `GRADLE_PUBLISH_KEY`
  - `GRADLE_PUBLISH_SECRET`
- [ ] Publish: `./gradlew publishPlugins`.

## Post-Release
- [ ] Verify plugin page on the Plugin Portal.
- [ ] Tag any related repository releases if needed.
