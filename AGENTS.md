# Repository Guidelines

## Project Structure & Module Organization
- `app/`: Android app (Jetpack Compose, navigation, DI). Manifest and resources in `app/src/main/`.
- `core/`: Shared layers — `data/`, `domain/`, `presentation/`, `common/`, `dgis/`, plus `core/test` (test utilities).
- `feature/<name>/`: Vertical slices with `data/`, `domain/`, `presentation/` (e.g., `feature/auth/presentation`).
- `service/<name>/`: Service clients and integration modules.
- `benchmark/`: Macrobenchmark tests.
- Build logic: `build-logic/` custom Gradle convention plugin.

## Build, Test, and Development Commands
- Build debug APK: `./gradlew assembleDebug`
- Install on device: `./gradlew :app:installDebug`
- Release bundle (minify/shrink): `./gradlew :app:bundleRelease` (outputs like `Yalla 0.1.0.aab`)
- Unit tests: `./gradlew test`
- Instrumented tests: `./gradlew connectedAndroidTest`
- Lint (Android Lint): `./gradlew lint`

## Coding Style & Naming Conventions
- Kotlin: official style (`kotlin.code.style=official`), JVM 11, AndroidX, Compose enabled.
- Indentation: 4 spaces; line width follow Kotlin defaults.
- Package: `uz.yalla.client` and feature subpackages.
- Module naming: `feature:<name>:(data|domain|presentation)`, `core:*`, `service:*`.
- UI/MVI patterns: use `FooRoute`, `FooScreen`, `FooViewModel`, plus `FooIntent`, `FooState`, `FooSideEffect` (example: `LoginState`, `LoginIntent`).

## Testing Guidelines
- Frameworks: JUnit4, Truth, MockK, Coroutines Test, AndroidX Test; Compose UI test manifest is available.
- Shared test deps in `core/test`; prefer reusing helpers from there.
- Location: place unit tests in `src/test/`, instrumented tests in `src/androidTest/`.
- Coverage: exercise ViewModels/intents and navigation paths; add minimal UI tests for critical flows.

## Commit & Pull Request Guidelines
- Use Conventional Commits: `feat:`, `fix:`, `refactor:`, `build:` with scopes (e.g., `feat(promocode): add activation flow`).
- PRs: include summary, linked issues, screenshots/screencasts for UI, and notes on testing/rollback. Ensure `assembleDebug`, `test`, `lint`, and (if changed) `connectedAndroidTest` pass locally.

## Security & Configuration Tips
- Do not commit secrets. Firebase config lives in `google-services.json`; use the Maps Secrets Gradle Plugin for API keys.
- Repositories include a corporate Maven host; network access may be required. Min SDK 26, target SDK 35.
