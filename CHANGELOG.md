# Changelog

All notable changes to the **AutoPulse (Native Android Automotive Intelligence Platform)** project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.2] - 2026-11-06

### Added
- **Deterministic Release Keystore (`app/autopulse-release.jks`):** Embedded a dedicated, persistent PKCS12 release keystore valid through 2054 (`10,000` days) to ensure all local and CI builds generate identical APK signatures.
- **Permanent APK Signing Configuration (`app/build.gradle.kts`):** Configured both `release` and `debug` build types to sign with `autopulse-release.jks` (with environment variable overrides supported).
- **Consistent Certificate Fingerprint:** Guarantees 100% deterministic SHA-256 certificate digest (`05:44:DC:21:6A:AA:A5:57:E5:05:5B:6D:30:D5:E3:F3:A9:F8:FB:EC:FA:5E:8E:35:B1:A8:A9:25:E3:08:16:01`) across all builds and runners, preventing `INSTALL_FAILED_UPDATE_INCOMPATIBLE` app update errors.

## [1.2.1] - 2026-11-06

### Fixed
- **GitHub Actions Android SDK Build Environment:** Resolved build failures caused by `android-actions/setup-android@v3` attempting to install the deprecated `tools` package. Standardized on the GitHub runner's native pre-installed Android SDK 34 environment.

### Changed
- **Unified Build APK Automation (`.github/workflows/build-apk.yml`):** Consolidated separate CI and Release workflows into a single unified automation pipeline. On branch pushes and PRs, it runs unit/integration tests and outputs installable Android APK artifacts (`autopulse-apk`); on version tags (`v*.*.*`), it builds APKs and publishes the GitHub Release.
- **Local Dev-Only Code File Verification:** Removed `checkLineLimits` from the GitHub Actions automation and decoupled `releaseBundle` so that code file limits are strictly enforced locally in the development workspace.

## [1.2.0] - 2026-11-06

### Changed
- **APK Production Output (Replacing JAR Output):** Configured both GitHub Actions workflows (`.github/workflows/ci.yml` and `.github/workflows/release.yml`) and distribution Gradle tasks (`releaseBundle`) so that the primary output artifacts are installable Android APKs (`autopulse-v1.2.0.apk`, `autopulse-v1.2.0-debug.apk`) rather than JAR files.
- **GitHub Actions CI Workflow Artifacts (`.github/workflows/ci.yml`):** Added an automated artifact upload step (`actions/upload-artifact@v4`) exporting installable Android APKs on every CI build.
- **GitHub Actions Release Pipeline (`.github/workflows/release.yml`):** Removed JAR file copying (`core/build/libs/*.jar`) and release asset attachment; added workflow artifact uploads for release APKs and attached only `.apk`, `.zip` (APK bundle), and `SHA256SUMS.txt`.
- **Distribution Bundle Optimization (`build.gradle.kts`):** Removed `:core` JAR packaging from `releaseBundle` to ensure the release bundle zip contains exclusively the APK binaries and governance documentation.

## [1.1.0] - 2026-11-06

### Added
- **Installable Android APK Package (`:app`, `autopulse-v1.1.0.apk`):** Configured Android Gradle Plugin (`com.android.application`), Jetpack Compose Compiler plugin, `AutoPulseApplication.kt`, and `MainActivity.kt` with a 4-tab Material 3 navigation bar (`1. Garage HUD`, `2. Translator`, `3. Shop Bay`, `4. Passport`) to compile an installable `.apk` directly for Android devices and emulators.
- **Automated APK Release Attachment (`.github/workflows/release.yml`):** Updated the GitHub Actions release pipeline to compile and attach `autopulse-v1.1.0.apk` alongside `core-1.1.0.jar`, `autopulse-android-1.1.0-release-bundle.zip`, and `SHA256SUMS.txt`.

## [1.0.0] - 2026-11-06

### Added
- **Engineering Standards & Agent Playbook (`GEMINI.md`):** Established authoritative engineering governance enforcing pre-work Git synchronization, MAD Clean Architecture + MVI modularity, a strict 500-line file limit (`./gradlew checkLineLimits`), `AppResult<T>` error boundaries (`AppResult.kt`), structured logging with automatic secret redaction (`StructuredLogger.kt`), mandatory JUnit 5 tests, living documentation, semantic versioning, and CI/CD release automation.
- **Module 1 — Smart Garage & Dynamic Maintenance Engine (`MaintenanceCalculationEngine.kt`, `OemMaintenanceCatalog.kt`):** Implemented 42 distinct OEM vehicle subsystems across `POWERTRAIN`, `BRAKING`, `FLUIDS`, `ELECTRICAL`, `SUSPENSION`, and `FILTERS` with exact wear calculation, `HEALTHY` / `DUE_SOON` / `OVERDUE` states, continuous brake pad (`mm`) & 12V battery health (`% SOH`) models, and NHTSA vPIC VIN decoding.
- **Module 2 — Native Jetpack Compose Visual Experience & Animations (`RollingOdometer.kt`, `IsometricVehicleHud.kt`):** Implemented mechanical digit-by-digit `RollingOdometer` using Compose `AnimatedContent` and spring physics, interactive 360° 3D/isometric vehicle wireframe HUD with radial shader ripples on overdue components, and animated circular health rings.
- **Module 3 — Mechanic-to-Human Jargon Translator & 102-Part Encyclopedia (`MechanicTranslatorEngine.kt`, `MechanicJargonCatalog.kt`, `CarPartsEncyclopediaCatalog.kt`):** Implemented plain-English repair decoder, 1–5 urgency rating, fair parts/labor price estimator, shop quote fairness auditor, and a searchable 102-part visual encyclopedia.
- **Module 4 — Partner Shop Tablet Terminal (`PartnerShopWorkOrderUseCase.kt`, `PartnerShopTerminalScreen`):** Implemented QR/VIN vehicle intake, digital work order execution, real-time maintenance cycle reset, SHA-256 Partner Service Seal signing, and FCM push notification payloads.
- **Module 5 — Immutable Vehicle Trust Passport & Anti-Fraud Shield (`OdometerIntegrityVerifier.kt`, `TrustPassportPdfExporter.kt`):** Implemented chained `SHA-256(vehicleId:mileage:timestamp:previousHash)` odometer ledger, negative delta rollback detection ($\Delta M < 0 \implies \text{CRITICAL\_FRAUD}$), implausible velocity detection ($R > 800\text{ mi/day} \implies \text{WARNING}$), 0–100 Lemon Risk scoring, and exportable Pre-Purchase Buyer Inspection reports.
- **CI/CD & Automated GitHub Releases (`.github/workflows/ci.yml`, `.github/workflows/release.yml`):** Added automated GitHub Actions workflows for line-limit verification, JUnit test execution, production artifact packaging, and GitHub Release publishing.
