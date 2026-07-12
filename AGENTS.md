# Repository Guidelines

## Project Structure & Module Organization
This is a multi-module Android project using Gradle Kotlin DSL.

- `app/` is the application entry point and owns the manifest, navigation, dependency injection, build types, and product flavors.
- `core/` contains shared models, domain logic, data sources, persistence, networking, Firebase Remote Config, UI/design-system components, and test utilities.
- `feature/` contains screen-focused modules such as `productlist`, `sale`, `clientdetail`, `signup`, and `subscription`.
- `build-logic/` contains convention plugins; dependency versions and aliases live in `gradle/libs.versions.toml`.
- Code is under `src/main/java`, resources under `src/main/res`, and local/device tests under `src/test` and `src/androidTest`.

## Build, Test, and Development Commands
Use the Gradle wrapper from the repo root.

- `./gradlew :app:assembleDebug` — build a debug APK.
- `./gradlew :app:installDebug` — install the debug app on a connected device/emulator.
- `./gradlew test` — run JVM unit tests across modules.
- `./gradlew :app:testDebugUnitTest` — run app module unit tests.
- `./gradlew connectedAndroidTest` — run instrumented tests on a device/emulator.
- `./gradlew :app:lint` — run Android Lint on the app module.

The app has an `edition` flavor dimension. If a task is ambiguous, find its flavor-specific form with `./gradlew :app:tasks`.

## Coding Style & Naming Conventions
- Use Kotlin, Jetpack Compose, and Gradle Kotlin DSL with 4-space indentation and standard Kotlin formatting.
- Name classes and composables in `PascalCase`; use `camelCase` for functions and properties. Screen modules commonly pair `FeatureScreen` with `FeatureViewModel`.
- Modules follow `:core:*` and `:feature:*` naming patterns.
- No formatter is configured; keep changes minimal, match nearby code, and run Android Lint before broad changes.

## Testing Guidelines
- Unit tests use JUnit 5 and MockK. Instrumented/UI tests use AndroidX Test, Espresso, and Compose UI testing.
- Keep tests in the module they cover, name classes `*Test.kt`, and use descriptive method names.
- Add or update tests for changed business logic and regressions. No minimum coverage threshold is enforced.

- Shared reusable Compose components that are not complete application screens belong in `core:ui`, not in a feature module.
- Reuse the date helpers in `core:common` (`DateUtils.kt`) instead of introducing feature-local date parsing or calculation utilities.
- Before creating a test class, look for an existing class covering the same feature or utility and add the test there. Keep UI tests in the owning feature module; place non-UI tests in the corresponding `core` module.

## Commit & Pull Request Guidelines
- Recent commits use concise imperative subjects such as `Fix: skip Crashlytics mapping upload by default`. Optional Conventional Commit prefixes are acceptable.
- For PRs, include a summary, testing notes, and screenshots/screen recordings for UI changes. Link related issues when applicable.

## Security & Configuration Tips
- `local.properties` and keystore data are environment-specific. Do not commit secrets.
- Ensure Android SDK paths are configured locally before building.
