# Repository Guidelines

## Project Structure & Module Organization
This is a multi-module Android project using Gradle Kotlin DSL.

- `app/`: application module (entry point, AndroidManifest, app-level DI).
- `core/`: shared layers such as `common`, `data`, `domain`, `designsystem`, `network`, `ui`, and `testing`.
- `feature/`: UI/feature modules (e.g., `productlist`, `salelist`, `login`).
- `build-logic/`: convention plugins and shared Gradle configuration.
- Tests live under `src/test` (unit) and `src/androidTest` (instrumented) in each module.
- UI assets/resources are under `src/main/res` in each module.

## Build, Test, and Development Commands
Use the Gradle wrapper from the repo root.

- `./gradlew :app:assembleDebug` — build a debug APK.
- `./gradlew :app:installDebug` — install the debug app on a connected device/emulator.
- `./gradlew test` — run JVM unit tests across modules.
- `./gradlew :app:testDebugUnitTest` — run app module unit tests.
- `./gradlew connectedAndroidTest` — run instrumented tests on a device/emulator.
- `./gradlew :app:lint` — run Android Lint on the app module.

## Coding Style & Naming Conventions
- Language: Kotlin (Jetpack Compose for UI) and Gradle Kotlin DSL.
- Indentation: 4 spaces; follow standard Kotlin style (class names `PascalCase`, functions/vars `camelCase`).
- Modules follow `:core:*` and `:feature:*` naming patterns.
- No explicit formatter/linter configuration is present; keep diffs minimal and consistent with nearby code.

## Testing Guidelines
- Unit tests use JUnit (see `src/test`).
- Instrumented tests use AndroidX test runner and Espresso (see `src/androidTest`).
- Keep new tests close to the module they cover; prefer naming `*Test.kt` and `*InstrumentedTest.kt`.

## Commit & Pull Request Guidelines
- Recent commits use short, imperative messages (e.g., "Implements …", "Fix …") and occasional Conventional Commit prefixes (`feat:`, `fix:`). Stick to concise, descriptive subjects.
- For PRs, include a summary, testing notes, and screenshots/screen recordings for UI changes. Link related issues when applicable.

## Security & Configuration Tips
- `local.properties` and keystore data are environment-specific. Do not commit secrets.
- Ensure Android SDK paths are configured locally before building.
