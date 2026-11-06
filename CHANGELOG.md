# Changelog

All notable changes to the **AutoPulse (Native Android Automotive Intelligence Platform)** project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
