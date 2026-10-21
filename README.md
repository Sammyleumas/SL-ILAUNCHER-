# SL-LAUNCHER

**Your World. Perfectly Organized.**
A native Android home-screen launcher (Kotlin + Jetpack Compose) with a premium glass + monochrome design.

Developed by **SL-TECHCO** — Abiodun Samuel Feranmi (SAMMY LEUMAS)
WhatsApp: 09078076906 · Email: abiodunsamuel112@gmail.com

> **Build status: NOT VERIFIED.** This Stage 1 source was written without access to a Gradle/Android build
> environment. It has not been compiled, installed, or tested. Run the CI workflow or the commands below
> and fix any compile errors before relying on it.

## Feature status

| Feature | Stage | Status |
|---|---|---|
| Home app registration (MAIN/HOME/DEFAULT + LAUNCHER) | 1 | Implemented, not built/tested |
| Real app discovery (PackageManager, narrow `<queries>`) | 1 | Implemented, not built/tested |
| App drawer, alphabetical, search-as-you-type | 1 | Implemented, not built/tested |
| App launching with failure handling | 1 | Implemented, not built/tested |
| Live refresh on install/uninstall/update | 1 | Implemented, not built/tested |
| Async icon loading + LRU cache | 1 | Implemented, not built/tested |
| Dark / Light / Monochrome theme (DataStore) | 1 | Implemented, not built/tested |
| Glass components (translucent fallback, no real blur yet) | 1 | Partially implemented |
| Clock/date on home (broadcast-driven) | 1 (early) | Implemented, not built/tested |
| Swipe-up opens drawer (fixed, not yet configurable) | 1 | Partially implemented |
| GitHub Actions CI + debug APK artifact | 1 | Written, not run |
| Unit tests (app filter) + 1 Compose UI test | 1 | Written, not run |
| Multi-page home, dock, folders, icon packs, wallpaper | 2 | Planned |
| Widgets, configurable gestures, recents, uninstall | 3 | Planned |
| Calendar, notes, weather, focus mode, unified search | 4 | Planned |
| Hidden apps, notification dots, usage, backup, privacy | 5 | Planned |
| Hardening, migrations, release polish | 6 | Planned (release workflow already drafted) |

## Requirements
JDK 17, Android SDK (platform 35). Min Android 8.0 (API 26). Toolchain: Gradle 8.10.2, AGP 8.7.3, Kotlin 2.0.21, Compose BOM 2024.10.01.

## Build
Windows: `gradlew.bat testDebugUnitTest` then `gradlew.bat assembleDebug`
Linux/macOS: `./gradlew testDebugUnitTest` then `./gradlew assembleDebug`
Output: `app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions
Push to GitHub. **Actions → Android CI → latest run → Artifacts → `SL-LAUNCHER-debug-apk`** (and `reports`).
Actions are pinned to major versions, not commit SHAs; pin SHAs before production distribution.

## Install and set as Home
1. Copy the APK to your phone, allow "Install unknown apps" for your file manager, install.
2. Press Home → choose **SL-LAUNCHER** → **Always** (or Settings → Apps → Default apps → Home app).
3. To go back: select your previous launcher in the same setting. To remove: uninstall SL-LAUNCHER after switching Home app.

## Release signing
`release-apk.yml` (manual or `v*` tag) requires GitHub secrets `SL_KEYSTORE_BASE64`, `SL_KEYSTORE_PASSWORD`, `SL_KEY_ALIAS`, `SL_KEY_PASSWORD`, and fails if any is missing. Locally, export `SL_KEYSTORE_PATH` and the other three as environment variables. Release never falls back to debug signing. Never commit keystores. Output: `app/build/outputs/apk/release/app-release.apk`.

## Privacy and permissions (Stage 1)
No permissions are requested. No network access. Package visibility uses a narrow launcher-intent query (no `QUERY_ALL_PACKAGES`). Backup is disabled (`allowBackup=false`) until the in-app backup arrives in Stage 5.

## Known limitations
Glass uses translucency only (no real blur yet). Android restricts third-party launchers from intercepting system gestures and the system Recents screen. Physical-device and Android-version testing has not been performed.

## Troubleshooting
- Gradle wrapper rejected: re-generate with `gradle wrapper --gradle-version 8.10.2` and verify via CI wrapper validation.
- App list empty: confirm the manifest `<queries>` block is intact.
