# AutoPulse — Native Android Automotive Intelligence Platform (Kotlin + Jetpack Compose)

[![AutoPulse Android Build](https://github.com/LeBill7/Demo/actions/workflows/build-apk.yml/badge.svg)](https://github.com/LeBill7/Demo/actions/workflows/build-apk.yml)
[![Version](https://img.shields.io/badge/version-1.2.1-00F2FE.svg)](./CHANGELOG.md)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF.svg)](https://kotlinlang.org)

**AutoPulse (`v1.2.1`)** is a native Android (Kotlin + Jetpack Compose Material 3) three-sided automotive intelligence platform engineered using Modern Android Development (MAD) Clean Architecture and Unidirectional Data Flow (MVI).

> **Download & Install the APK:** Grab `autopulse-v1.2.1.apk` directly from the [GitHub Releases page](https://github.com/LeBill7/Demo/releases/tag/v1.2.1) or build it locally with `./gradlew :app:assembleRelease` (`app/build/outputs/apk/release/app-release.apk`) or `./gradlew :app:assembleDebug` (`app/build/outputs/apk/debug/app-debug.apk`).

---

## Three-Sided Automotive Ecosystem

### 1. The Driver's Android App (Kotlin + Jetpack Compose)
- **Smart Garage & NHTSA vPIC VIN Decoding:** Supports 1980–Present vehicles across `FWD`, `RWD`, `AWD`, `FOUR_WD` drivetrains and `GASOLINE`, `DIESEL`, `HYBRID`, `PHEV`, and `EV` powertrains.
- **42-Subsystem Dynamic Maintenance Engine (`MaintenanceCalculationEngine.kt`):** Recalculates wear across `POWERTRAIN`, `BRAKING`, `FLUIDS`, `ELECTRICAL`, `SUSPENSION`, and `FILTERS` upon every odometer update:
  - **HEALTHY (`0%–74%` wear):** Electric Cyan / Emerald theme.
  - **DUE_SOON (`75%–99%` wear):** Amber caution alert.
  - **OVERDUE (`≥100%` wear):** Crimson pulse with safety impact disclaimer.
- **Native Jetpack Compose Motion & 3D HUD:**
  - `RollingOdometer.kt`: Digit-by-digit vertical tumbler roll using Compose `AnimatedContent`, `slideInVertically`, and spring physics (`stiffness = 300f, dampingRatio = 0.75f`).
  - `IsometricVehicleHud.kt`: 360° rotatable 3D/isometric vehicle wireframe HUD rendered via Compose `Canvas` / `DrawScope` with touch hotspots, pulsing radial shader ripples on `OVERDUE` components, and `AnimatedHealthRing` gauges.

### 2. The "Mechanic-to-Human" Jargon Translator & 102-Part Encyclopedia
- **AI Jargon Decoder (`MechanicTranslatorEngine.kt`):** Translates mechanic quotes (e.g., *"Blown Head Gasket"*, *"Differential Pinion Seal Leak"*, *"Tie Rod Play"*, *"Catalytic Converter P0420"*) into plain-English explanations, failure analogies, urgency scores (`1..5`), fair parts/labor price ranges, quote fairness verdicts, and smart questions to ask the mechanic.
- **Car Parts Visual Encyclopedia (`CarPartsEncyclopediaCatalog.kt`):** Searchable catalog of **102 essential automotive parts** categorized by system.

### 3. Partner Shop Tablet Terminal & Immutable Vehicle Trust Passport
- **Partner Shop Workstation (`PartnerShopWorkOrderUseCase.kt`, `PartnerShopTerminalScreen.kt`):** CameraX QR/VIN intake, one-tap work order execution, automatic maintenance cycle reset to `0%` wear, SHA-256 Partner Service Seal signing, and FCM push notification dispatch.
- **Immutable Trust Passport & Anti-Fraud Shield (`OdometerIntegrityVerifier.kt`, `TrustPassportPdfExporter.kt`):**
  - Chained `SHA-256(vehicleId:mileage:timestamp:previousHash)` odometer ledger.
  - **Rollback Detection:** Flags `CRITICAL_FRAUD` whenever $\Delta M = M_{t_2} - M_{t_1} < 0$.
  - **Implausible Velocity Detection:** Flags `WARNING` whenever travel rate $R = \frac{\Delta M}{\Delta t_{\text{days}}} > 800\text{ mi/day}$.
  - **0–100 Trust & Lemon Risk Score** + exportable Pre-Purchase Buyer Inspection Report.

---

## Build, Verification & Test Commands

```bash
# 1. Enforce 500-Line File Limit across all repository files (GEMINI.md Rule #2)
./gradlew checkLineLimits

# 2. Execute full JUnit 5 & Coroutine automated test suite
./gradlew test

# 3. Build installable Android APKs and Release Bundle
./gradlew buildApk releaseBundle
```

---

## Repository Governance & Living Documentation

- **[`GEMINI.md`](./GEMINI.md):** Authoritative engineering standards & pre/post-task checklists for all contributors and AI agents.
- **[`docs/ARCHITECTURE.md`](./docs/ARCHITECTURE.md):** Detailed Clean Architecture + MVI module guide and mathematical specifications.
- **[`CHANGELOG.md`](./CHANGELOG.md):** Semantic versioning release history.
