# SalesB - Sales Management Android Application

## Project Overview
SalesB is a modern Android application for sales management, built with a multi-module architecture following the "Now in Android" (NiA) pattern. It utilizes Clean Architecture principles to ensure scalability, testability, and maintainability.

### Core Technologies
- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/compose)
- **Dependency Injection:** [Hilt](https://dagger.dev/hilt/)
- **Architecture:** Clean Architecture with MVI/MVVM-like patterns.
- **Local Database:** [Room](https://developer.android.com/training/data-storage/room)
- **Networking:** [Retrofit](https://square.github.io/retrofit/) with [OkHttp](https://square.github.io/okhttp/) and [Gson](https://github.com/google/gson)
- **Asynchronous Programming:** [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
- **Data Persistence:** [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Preferences)
- **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
- **Reports:** [iText7](https://itextpdf.com/products/itext-7) (PDF generation)
- **Printer Support:** ESC/POS Thermal Printer library
- **Other Services:** Firebase (Crashlytics, Analytics, Remote Config), Google Play Billing

## Architecture
The project is divided into three main layers across multiple modules:

### 1. App Module (`:app`)
The entry point of the application. It orchestrates the navigation and ties all features together.

### 2. Feature Modules (`:feature:*`)
Each module represents a specific functional area of the app (e.g., `:feature:home`, `:feature:productlist`).
- **UI:** Built entirely with Jetpack Compose.
- **ViewModels:** Handle UI state and interact with UseCases from the domain layer.

### 3. Core Modules (`:core:*`)
Reusable components and business logic shared across features.
- `:core:domain`: Contains the business logic (UseCases) and domain models.
- `:core:data`: Implements repositories that fetch data from network or database.
- `:core:database`: Room database definition, entities, and DAOs.
- `:core:network`: Retrofit API interfaces and network data sources.
- `:core:model`: Shared data models used throughout the app.
- `:core:designsystem`: Common UI components, themes, and design tokens.
- `:core:common`: General utilities and base classes.
- `:core:print`: Logic for PDF generation and thermal printing.

## Building and Running

### Prerequisites
- Android Studio Ladybug or newer.
- JDK 17+

### Key Commands
- **Build Project:** `./gradlew build`
- **Run App (Debug):** `./gradlew :app:assembleDebug`
- **Run Tests:** `./gradlew test`
- **Lint Check:** `./gradlew lint`

> **Note:** For release builds, you need a `local.properties` file with valid signing configuration (refer to `app/build.gradle.kts`).

## Development Conventions

### Build System
- **Gradle Kotlin DSL:** All build files use `.gradle.kts`.
- **Version Catalogs:** Dependencies are managed in `gradle/libs.versions.toml`.
- **Convention Plugins:** Common build configurations are abstracted into plugins located in `build-logic/convention`.

### Coding Standards
- **Architecture:** Always follow the Clean Architecture layers. Business logic belongs in `UseCases` in `:core:domain`.
- **Dependency Injection:** Use Hilt for all dependency injections.
- **UI:** Use Jetpack Compose for all new UI components. Follow the established `designsystem` for themes and colors.
- **State Management:** Use `StateFlow` in ViewModels to expose UI state to Compose screens.
- **Testing:** Add unit tests for UseCases and ViewModels. Use `:core:testing` for shared test utilities.

### Naming Conventions
- **Packages:** `com.groupec.salesb.*`
- **UseCases:** `[Verb][Entity]UseCase` (e.g., `GetProductUseCase`).
- **ViewModels:** `[Feature]ViewModel` (e.g., `HomeViewModel`).
- **Screens:** `[Feature]Screen` (e.g., `HomeScreen`).
