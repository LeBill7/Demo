# AutoPulse Design System & UI/UX Design Language

> **Authoritative Specification:** This document defines the core design language, chromatic palette, responsive viewport rules (including the **250dp ultra-compact breakpoint**), typography scale, and human-centric UX principles governing the entire AutoPulse platform.

---

## 1. Core UX Philosophy

1. **Progressive Disclosure Over Information Dumping:**
   Never dump 42 raw subsystem rows, unformatted ASCII ledgers, or blank search boxes onto the user. Always present a high-level **Hero Status Card**, prioritize **Immediate Action Items** (`OVERDUE` and `DUE_SOON`), and tuck healthy or secondary details into clean, collapsible drawers or category chips.
2. **Action-First Guidance:**
   Every screen must answer: *"What is happening?"*, *"Why does it matter?"*, and *"What should I do right now?"*.
3. **Adaptive Across Any Screen ($\ge 250\text{dp}$):**
   The UI must seamlessly render and remain 100% usable on ultra-narrow displays (smartwatches, automotive sub-gauges, narrow split-screen viewports at 250dp) without text clipping, horizontal overflowing, or broken touch targets.
4. **Cryptographic Transparency with Human Warmth:**
   Translate complex SHA-256 hash chains, odometer ledgers, and mechanic jargon into plain English, intuitive visual gauges, and relatable analogies.

---

## 2. Color Palette & Chromatic Hierarchy

AutoPulse employs an **Obsidian Cockpit** aesthetic inspired by modern automotive HUDs, aerospace telemetry, and high-contrast night-mode dashboards.

```
┌────────────────────────────────────────────────────────────────────────┐
│  Background: #070B14 (Deep Obsidian)                                   │
│  Surface:    #0E1626 (Elevated Instrument Cluster)                     │
│  Border:     #1E293B (Hairline Stroke)                                 │
│                                                                        │
│  [ Primary Cyan ]   [ Success Emerald ]  [ Warning Amber ]  [ Crimson ]│
│     #00F2FE              #10B981             #F59E0B         #F43F5E   │
│  (Brand / Pristine) (Healthy / Seal)       (Due Soon)       (Overdue)  │
└────────────────────────────────────────────────────────────────────────┘
```

### Color Token Table

| Token Name | Hex Code | Semantic Role | Usage Context |
| :--- | :--- | :--- | :--- |
| `BackgroundDark` | `#070B14` | Cockpit Base | Screen root background, canvas depth |
| `SurfaceDark` | `#0E1626` | Card / Elevation | Cards, dialog surfaces, bottom nav bar, containers |
| `SurfaceBorder` | `#1E293B` | Hairline Contrast | 1dp outline on cards, dividers, unfocused borders |
| `SurfaceHover` | `#1A2438` | Interactive State | Pressed chips, hovered cards, active tab pill |
| `PrimaryCyan` | `#00F2FE` | Brand / Pristine | Hero highlights, selected tab indicator, 3D HUD wireframe |
| `SuccessEmerald` | `#10B981` | Healthy / Verified | 0–74% wear state, certified SHA-256 seal, fair price meter |
| `WarningAmber` | `#F59E0B` | Caution / Due Soon | 75–99% wear state, moderate urgency, upcoming service alerts |
| `CriticalCrimson` | `#F43F5E` | Immediate Danger | $\ge 100\%$ overdue, rollback fraud alert, emergency repair |
| `AccentViolet` | `#8B5CF6` | Partner Bay / Seal | Technician bay badge, certified service credentials |
| `TextPrimary` | `#F8FAFC` | Main Readability | Headlines, critical metrics, high-contrast labels |
| `TextSecondary` | `#94A3B8` | Contextual Guide | Explanations, analogies, descriptions, helper text |
| `TextTertiary` | `#64748B` | Subtle Metadata | Timestamps, VIN codes, SHA-256 hash digests |

---

## 3. Responsive Breakpoints & 250dp Layout Rules

AutoPulse implements three responsive viewport classes in `AdaptiveLayoutUtils.kt`:

| Class | Viewport Width ($W$) | Target Form Factors | Adaptive Behavior |
| :--- | :--- | :--- | :--- |
| **`TINY`** | $W < 300\text{dp}$ | Wear OS wrist HUD, 250dp auto sub-gauges, mini split-screen | 8dp screen insets; all multi-column rows stack vertically; scaled odometer (18dp/digit); compact 4-tab bar |
| **`COMPACT`**| $300\text{dp} \le W < 600\text{dp}$ | Standard Android smartphones (portrait) | 16dp screen insets; standard odometer (26dp/digit); horizontal telemetry rows |
| **`EXPANDED`**| $W \ge 600\text{dp}$ | Foldables unfolded, tablets, automotive dash displays | Multi-pane grid; 2-column dashboard layout; full 3D wireframe canvas |

### Mandatory Rules for 250dp Screen Width:
1. **Screen Inset Restriction:** Reduce horizontal padding from `16dp` down to `8dp` whenever $W < 300\text{dp}$. This preserves 234dp of usable width instead of choking down to 218dp.
2. **Never Place Fixed-Width Widgets Side-by-Side:**
   - *Prohibited:* Placing `RollingOdometer` (160dp) + `AnimatedHealthRing` (64dp) in a single horizontal `Row` ($160 + 64 + \text{paddings} > 250\text{dp}$).
   - *Required:* Use `AdaptiveRowOrColumn` so they automatically stack vertically on `TINY` viewports.
3. **No 4-Button Text Rows:**
   - Bottom navigation buttons must never display long text like `"1. Garage HUD"` across 4 equal-width buttons on 250dp screens. Use compact labels (`Garage`, `Copilot`, `Bay`, `Passport`) with clear active indicator pills.
4. **Digit Scalability:**
   - Rolling odometer tumbler width is scaled down from `26dp` to `18dp` with `18sp` typography when $W < 300\text{dp}$, keeping total odometer width strictly $< 170\text{dp}$.
5. **Touch Targets:**
   - All interactive touch targets must maintain a minimum height/width of `44dp` to ensure comfortable interaction even on small displays.

---

## 4. Typography Scale

| Style Role | Font Family | Size | Weight | Line Height | Usage |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `DisplayHero` | Sans-Serif | 22sp | Black (900) | 28sp | Hero health score, vehicle title |
| `TitleMedium` | Sans-Serif | 16sp | Bold (700) | 22sp | Section titles, card headlines |
| `BodyRegular` | Sans-Serif | 13sp | Normal (400) | 18sp | Plain-English explanations, analogies |
| `BodyMuted` | Sans-Serif | 11sp | Medium (500) | 15sp | Safety disclaimers, subtitle notes |
| `TelemetryMono`| Monospace | 18sp / 28sp| Black (900) | 32sp | Odometer numbers, speed, dollar quotes |
| `HashDigestMono`| Monospace | 10sp | Bold (700) | 14sp | SHA-256 blocks, VIN numbers, seals |

---

## 5. Screen UX Blueprints

### 1. Driver Garage Screen (The Vehicle Command Center)
- **Top Vehicle Header & Quick Switcher:** Displays active vehicle with one-tap switcher between garage vehicles.
- **Hero Status Card:** High-visibility banner summarizing condition (e.g. 🚨 *"2 Overdue Services"* or ✅ *"All Systems Optimal"*).
- **Adaptive Telemetry:** Rolling Odometer and circular health ring with tap-to-update guided stepper modal (`+100`, `+500`, `+1,000 MI` and custom entry).
- **3D HUD Wireframe:** Interactive rotating vehicle canvas with zone filter chips (`All`, `Engine`, `Brakes`, `Cabin`).
- **Priority Action Queue:**
  1. 🔴 **Immediate Attention Required** (`OVERDUE` items highlighted in Crimson with safety warnings).
  2. 🟡 **Due Soon** (`DUE_SOON` items at 75–99% wear).
  3. 🟢 **Healthy Subsystems (X items)** (Collapsed in clean accordion to eliminate cognitive overload).
- **Category Filter Chips:** Horizontal scrolling chips (`All`, `Powertrain`, `Braking`, `Fluids`, `Electrical`, `Suspension`, `Filters`).

### 2. Mechanic Jargon Translator (The Repair Copilot)
- **Preset Quick-Pick Chips:** Common shop repairs (`Blown Head Gasket`, `Differential Leak`, `Tie Rod Play`, `Catalytic P0420`, `Timing Belt`).
- **Quote Fairness Meter:** Real-time visual gauge displaying whether a quote is **Fair**, **Moderate**, or **Overpriced** compared to OEM parts & labor database.
- **Plain-English Breakdown:** "What is this part?", "Everyday Analogy", and "Safe to drive?" verdict.
- **"Questions to Ask Your Mechanic" Checklist:** Interactive checklist giving drivers confidence at the shop.
- **Searchable 102-Part Encyclopedia Sheet:** On-demand search for essential car parts.

### 3. Partner Shop Bay Terminal (Technician Tablet / Mobile Bay)
- **Step 1: Vehicle Intake:** Quick VIN and incoming odometer verification.
- **Step 2: Service Checklist:** Multi-select checklist of vehicle maintenance items with automatic price calculation.
- **Step 3: Certification & SHA-256 Seal:** Technician notes, invoice confirmation, and one-tap cryptographic signing with animated seal confirmation.

### 4. Trust Passport (The Anti-Fraud Shield)
- **0–100 Trust Score Gauge:** High-impact circular gauge with Lemon Risk status badge (`LOW RISK`, `MODERATE`, `CRITICAL FRAUD`).
- **Anti-Fraud Shield Checklist:** Instant verification of SHA-256 chain, negative delta rollback checks, and daily velocity checks.
- **Verified Ledger Timeline:** Visual cards of each verified odometer entry with cryptographic hash stamp.
- **Export Inspection Summary:** Instant shareable/exportable buyer report.
