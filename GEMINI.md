# GEMINI.md — Strict Engineering Standards & Agent Playbook

> **AUTHORITATIVE SYSTEM DIRECTIVE:** This document defines the mandatory, non-negotiable engineering standards, architectural constraints, quality gates, and operational workflows for all AI agents and engineers working on the **AutoPulse (Native Android Automotive Intelligence Platform)** repository. Every task, pull request, and commit must strictly comply with all rules below.

---

## Mandatory Agent Checklists

### Pre-Task Checklist (Execute Before Writing Any Code)
- [ ] **1. Synchronize Git Working Tree:** Run `git status` and `git pull --rebase` (or `git pull`) before reading files, planning, or writing any code. Ensure the working tree is clean and free of merge conflicts.
- [ ] **2. Verify Architectural Boundaries:** Review target modules (`:core` Domain/Data engines vs. `:app` Jetpack Compose MVI Presentation layer) and preserve strict Clean Architecture separation of concerns.
- [ ] **3. Check File Line Counts:** Verify no existing or planned source file will exceed the **500-line hard limit**. Proactively split classes, Composables, or catalogs before approaching 500 lines.
- [ ] **4. Define Error, Logging & Test Strategy:** Identify all I/O, network, database, parsing, and security operations requiring `AppResult<T>` error boundaries, `StructuredLogger` instrumentation, and JUnit test coverage.

### Post-Task Checklist (Execute Before Every Commit & Push)
- [ ] **1. Enforce 500-Line Limit:** Run `./gradlew checkLineLimits` to verify every source file in the repository is strictly $\le 500$ lines.
- [ ] **2. Audit Error Handling & Structured Logging:** Confirm zero unprotected I/O or parsing calls and zero raw `println` statements in production code; ensure `StructuredLogger` (`DEBUG`, `INFO`, `WARN`, `ERROR`) is used with sensitive data redaction.
- [ ] **3. Run Full Automated Test Suite:** Execute `./gradlew test` (unit and integration tests covering happy paths, boundary conditions, and error paths) and confirm 100% pass rate.
- [ ] **4. Verify Complete Production Build:** Execute `./gradlew check build releaseBundle` locally and resolve all compiler errors, lint warnings, or runtime issues before proceeding.
- [ ] **5. Update Living Documentation:** Update `README.md`, `docs/ARCHITECTURE.md`, and relevant module documentation in the same commit whenever functionality, structure, configuration, or APIs change.
- [ ] **6. Bump Semantic Version & Changelog:** Increment `MAJOR.MINOR.PATCH` consistently across `version.properties`, `build.gradle.kts`, and `AppVersion.kt`. Document all changes in `CHANGELOG.md`.
- [ ] **7. Stage, Commit, Tag & Push:** Stage all files (`git add -A`), commit using Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`), create a matching Git tag (`vX.Y.Z`), and push both branch and tags (`git push origin main --tags`).

---

## 1. Pre-Work Git Synchronization

- **Always Pull First:** Before reading files, planning, or writing any code, run:
  ```bash
  git status
  git pull --rebase
  ```
- Ensure the local working tree is synchronized with the latest remote changes on `origin/main` and free of merge conflicts.

---

## 2. Architecture & Modularity

### Strict Architectural Integrity (Modern Android Development — Clean Architecture + MVI)
AutoPulse is structured as a multi-module Kotlin 2.0+ & Jetpack Compose project with strict unidirectional data flow (UDF / MVI):

```text
/workspaces/Demo
├── .github/workflows/       # CI & Automated GitHub Release pipelines (ci.yml, release.yml)
├── docs/                    # Living architecture & module documentation (ARCHITECTURE.md)
├── core/                    # Pure Kotlin 2.0 Domain, Cryptographic Security, & Data Layer
│   └── src/
│       ├── main/kotlin/com/autopulse/app/
│       │   ├── core/        # AppVersion, StructuredLogger, AppResult<T> & AppError hierarchy
│       │   ├── domain/      # Models, 42-Rule OEM Catalog, 102-Part Encyclopedia, UseCases,
│       │   │                # MaintenanceCalculationEngine, OdometerIntegrityVerifier,
│       │   │                # MechanicTranslatorEngine
│       │   └── data/        # Offline-first AutoPulseRepository & NHTSA VIN Decoder service
│       └── test/kotlin/     # Comprehensive JUnit 5 Unit & Integration Test Suites
└── app/                     # Native Android Jetpack Compose UI & MVI Presentation Layer
    └── src/main/
        ├── AndroidManifest.xml
        └── kotlin/com/autopulse/app/presentation/
            ├── components/  # RollingOdometer.kt, IsometricVehicleHud.kt
            ├── screens/     # DriverGarageScreen.kt, MechanicTranslatorScreen.kt,
            │                # PartnerShopTerminalScreen.kt, TrustPassportScreen.kt
            ├── viewmodel/   # AutoPulseViewModel.kt (StateFlow<UiState> + Channel<UiEffect>)
            └── MainActivity.kt
```

### 500-Line File Limit
- **Hard Ceiling:** No source file may exceed **500 lines of code**.
- The `./gradlew checkLineLimits` task automatically inspects all project source files and fails the build if any file exceeds 500 lines.

---

## 3. Error Handling & Structured Logging

### Comprehensive Error Catching
- Never leave operations unprotected or fail silently.
- Wrap all I/O, network requests, database operations, parsing, and cryptographic operations in `AppResult<T>` (`AppResult.Success` / `AppResult.Failure`) using `runCatchingLogged` boundary handlers with meaningful `AppError` codes and fallback states.

### Structured Logging
- Use `StructuredLogger` (`com.autopulse.app.core.logging.StructuredLogger`) across all modules with `DEBUG`, `INFO`, `WARN`, and `ERROR` severity levels.
- Log key state transitions, odometer hash verifications, work order certifications, and error contexts with structured key-value metadata.
- Never use raw `println` in production code, and ensure sensitive keys (`password`, `token`, `secret`, `apiKey`, `ssn`) are automatically redacted.

---

## 4. Testing & Quality Assurance

### Mandatory Feature Tests
- Every new feature, engine, use case, or bug fix must include automated JUnit tests under `core/src/test/kotlin/` covering:
  1. **Happy paths** (standard OEM wear calculation, valid SHA-256 hash chain, mechanic jargon translation).
  2. **Edge cases** (0-mile vehicles, exact 75% `DUE_SOON` and 100% `OVERDUE` boundaries, 800 mi/day velocity boundary).
  3. **Error-handling paths** (negative mileage inputs, invalid VIN strings, blank mechanic terms, tampered hashes).

### Pre-Push Verification
- Before committing or pushing any changes, run `./gradlew checkLineLimits test build releaseBundle` locally and ensure zero test failures, linter errors, or build warnings.

---

## 5. Living Documentation

- **Continuous Documentation:** Maintain `README.md` and `docs/ARCHITECTURE.md` with up-to-date architecture diagrams, mathematical specifications, setup instructions, and module guides.
- **Update on Every Commit:** Every commit that alters functionality, structure, configuration, or APIs must update the corresponding documentation in the same commit.

---

## 6. Versioning, Release Notes & Git Push Workflow

- **Semantic Versioning Bump:** Upon completing a task and passing all local builds/tests, increment `MAJOR.MINOR.PATCH` across `version.properties`, `build.gradle.kts`, and `AppVersion.kt`.
- **Release Notes & Changelog:** Record all additions, fixes, and breaking changes under the new version heading in `CHANGELOG.md`.
- **Stage, Commit, Tag & Push:**
  1. Stage all code, test, documentation, and version updates (`git add -A`).
  2. Commit using Conventional Commits formatting (`feat(autopulse): ...`).
  3. Create a matching Git version tag (`git tag vX.Y.Z`).
  4. Push both the branch and tags to the remote repository (`git push origin main --tags`).

---

## 7. CI/CD & Automated GitHub Releases

- **Continuous Integration (`.github/workflows/ci.yml`):** Automatically executes `./gradlew checkLineLimits :core:test :app:assembleDebug :app:assembleRelease releaseBundle` on every push and pull request to `main`, publishing installable APK artifacts.
- **Automated GitHub Releases (`.github/workflows/release.yml`):** Automatically triggers when a semantic version tag (`v*.*.*`) is pushed, builds production artifacts (`autopulse-vX.Y.Z.apk`, `autopulse-vX.Y.Z-debug.apk`, `autopulse-android-vX.Y.Z-release-bundle.zip`, and `SHA256SUMS.txt`), extracts release notes from `CHANGELOG.md`, and publishes a GitHub Release with compiled APK artifacts attached.
