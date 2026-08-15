# Repository Guidelines

## Project Structure & Module Organization
This is a multi-module Android project using Gradle Kotlin DSL.

- `app/` is the application entry point and owns the manifest, navigation, dependency injection, build types, and client definitions under `app/clients/`.
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

## Client Configuration
- Keep one version-controlled configuration per client in `app/clients/<client-id>.json`. Do not put client definitions or feature flags directly in `app/build.gradle.kts`.
- Follow the schema and private/Store flavor example in `app/clients/README.md`. Client-specific Android code and resources belong in `app/src/<client-id>/`.
- Keep the shared debug and release server roots in the `SERVER_URL` build-type fields in `app/build.gradle.kts`. Use each client's stable `backendId` to derive its API and upload URLs.
- Gradle reads the JSON and creates product flavors through `build-logic/convention/src/main/kotlin/com/groupec/salesb/clients/ClientFlavors.kt`.
- When adding a JSON field that must be available at runtime, update the complete typed configuration chain:
  1. Add the value to every applicable client JSON.
  2. Add the property to the corresponding JSON model in `ClientFlavors.kt`.
  3. Generate its `BuildConfig` field in `applyClientFlavor`.
  4. Expose it through `core/config/src/main/java/com/groupec/cleanarchitecture/core/config/AppConfig.kt` and map it in `app/src/main/java/com/groupec/salesb/appconfig/AppConfigImpl.kt`.
- Fields used only while configuring Gradle, such as `applicationId` or `sourceSet`, do not belong in `AppConfig`.
- After changing client configuration, confirm the generated tasks and compile an affected variant, for example with `./gradlew :app:tasks --all` and `./gradlew :app:compileSalesbDebugKotlin`.
- Never store secrets, signing credentials, or private keys in client JSON files.

## Coding Style & Naming Conventions
- Use Kotlin, Jetpack Compose, and Gradle Kotlin DSL with 4-space indentation and standard Kotlin formatting.
- Name classes and composables in `PascalCase`; use `camelCase` for functions and properties. Screen modules commonly pair `FeatureScreen` with `FeatureViewModel`.
- Modules follow `:core:*` and `:feature:*` naming patterns.
- No formatter is configured; keep changes minimal, match nearby code, and run Android Lint before broad changes.

## Compose UI Architecture
- The application-level `Scaffold` lives in `app/src/main/java/com/groupec/salesb/ui/MainScreen.kt`. Do not add another `Scaffold` inside a feature screen; configure shared top bars, back actions, bottom navigation, FAB visibility, and route titles from the application shell when needed.
- Feature screens orchestrate ViewModel state, navigation callbacks, lifecycle effects, and layout. Keep business rules in ViewModels, use cases, domain models, or repositories.
- Modify the existing screen structure before extracting a new private root/content composable. Extract one only when it is reused, supports an actual preview or UI-test seam, or clearly reduces substantial layout complexity; do not add a wrapper solely to separate state collection from otherwise single-use content.
- Before adding any other private feature composable, check whether it should reuse or extend an existing component.
- Put cross-feature reusable UI in `core/ui`. Put reusable design-system primitives such as buttons, text fields, cards, dialogs, images, titles, switches, and loading/error components in `core/designsystem`.
- Keep a component inside its `feature:*` module when it is specific to that feature and has no meaningful cross-feature reuse.
- Native Compose primitives such as `Text`, `Spacer`, `Row`, `Column`, `Box`, and lazy layouts may be used directly when they do not duplicate an existing project component.
- Prefer immutable inputs and event callbacks for reusable composables. When existing screen-owned mutable collections are required for coordinated behavior, preserve their single source of truth and do not remove them without tracing every consumer.
- Keep production composables before the first `@Preview` in a file and group previews at the end. Preview-only sample values must not leak into production behavior.

## Compose State & ViewModels
- Declare injected ViewModel constructor dependencies as `private val` unless they intentionally form part of the public API.
- Store changing ViewModel state in private `MutableStateFlow` instances and expose read-only `StateFlow` values. Do not introduce Compose `mutableStateOf` in ViewModels.
- Reserve `remember`, `rememberSaveable`, and Compose `mutableStateOf` for local composable UI state.
- For new or modified flow collection in composables, prefer `collectAsStateWithLifecycle` when the dependency and flow type support it; do not perform unrelated mass migrations of existing `collectAsState` calls.
- Reserve `UiState` and `FormUIState` for changing interface states such as idle, loading, error, success, selection, and mutable form progress. Use domain or data models for immutable business data.
- Values representing production business data, including prices, totals, quantities, stock, identifiers, phone numbers, and statuses, must come from a model, ViewModel, navigation argument, use case, repository, client configuration, or resource rather than being hardcoded in a screen.
- When removing a setting or state transition, trace its UI state, dependent flags, persistence, backend mapping, existing-user migration behavior, and tests before deleting the underlying method.

## Resources, Theme & Assets
- Keep user-facing strings in Android resources under `src/main/res/values/strings.xml` and maintain the corresponding French translations under `values-fr/strings.xml`. Avoid introducing hardcoded user-facing text in composables.
- Reuse `AppIcons`, existing `ImageVector` icons, or XML vector drawables for simple tintable monochrome pictograms. Keep raster assets for photographs, complex illustrations, and multicolored brand logos.
- Reuse `MaterialTheme.colorScheme`, `MaterialTheme.typography`, and existing named tokens from `core/designsystem` before adding raw colors or text styles to a screen.
- Add broadly reused colors and typography roles to the central design system instead of creating parallel feature theme objects. Keep changes compatible with the app's currently configured theme behavior.
- For screenshot or Figma work, preserve visual intent while following the existing module boundaries, reusable components, semantic theme roles, and accessibility requirements.

## Responsive UI
- Use the existing window-size-class-derived `isExpandedWidth` signal for compact versus expanded layouts. Prefer available width over device-name checks for new responsive decisions.
- Keep compact and expanded layouts behaviorally consistent unless requirements explicitly differ. When they use different visual components, preserve shared quantity, stock, selection, validation, loading, and error behavior.
- After changing a responsive branch, verify both compact and expanded call paths and remove only parameters that are no longer consumed anywhere in that branch.

## Testing Guidelines
- Unit tests use JUnit 5 and MockK. Instrumented/UI tests use AndroidX Test, Espresso, and Compose UI testing.
- Keep tests in the module they cover, name classes `*Test.kt`, and use descriptive method names.
- Add or update tests for changed business logic and regressions. No minimum coverage threshold is enforced.
- Test ViewModels through their public state and events rather than private implementation details.
- Prefer JVM unit tests for business logic and Compose UI tests for behavior that requires rendering or interaction.
- When removing an option or state transition, update obsolete tests and add coverage for the new invariant, including existing persisted values when relevant.

## Commit & Pull Request Guidelines
- Recent commits use concise imperative subjects such as `Fix: skip Crashlytics mapping upload by default`. Optional Conventional Commit prefixes are acceptable.
- For PRs, include a summary, testing notes, and screenshots/screen recordings for UI changes. Link related issues when applicable.

## Security & Configuration Tips
- `local.properties` and keystore data are environment-specific. Do not commit secrets.
- Ensure Android SDK paths are configured locally before building.
