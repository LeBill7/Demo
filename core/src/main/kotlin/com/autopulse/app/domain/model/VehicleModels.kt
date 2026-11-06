package com.autopulse.app.domain.model

enum class ComponentCategory {
    POWERTRAIN,
    BRAKING,
    FLUIDS,
    ELECTRICAL,
    SUSPENSION,
    FILTERS
}

enum class UrgencyLevel {
    ROUTINE,
    RECOMMENDED,
    CRITICAL_SAFETY
}

enum class HealthState {
    HEALTHY,    // 0% - 74% wear
    DUE_SOON,   // 75% - 99% wear
    OVERDUE     // >= 100% wear
}

enum class DrivetrainType {
    FWD, RWD, AWD, FOUR_WD
}

enum class FuelType {
    GASOLINE, DIESEL, HYBRID, PHEV, EV
}

data class Hotspot3D(
    val x: Float,
    val y: Float,
    val z: Float,
    val zone: String
)

data class MaintenanceRule(
    val id: String,
    val title: String,
    val category: ComponentCategory,
    val intervalMiles: Int,
    val criticalThresholdMiles: Int,
    val description: String,
    val urgencyLevel: UrgencyLevel,
    val estimatedCostUsd: Int = 120,
    val hotspot: Hotspot3D = Hotspot3D(0f, 0f, 0f, "Engine Bay")
)

data class ComponentHealthStatus(
    val ruleId: String,
    val title: String,
    val category: ComponentCategory,
    val milesSinceService: Int,
    val intervalMiles: Int,
    val remainingMiles: Int,
    val wearPercentage: Float, // 0.0f to 1.0f+
    val state: HealthState,
    val safetyDisclaimer: String = "",
    val estimatedCostUsd: Int = 120,
    val hotspot: Hotspot3D = Hotspot3D(0f, 0f, 0f, "Engine Bay")
)

data class ContinuousWearTelemetry(
    val frontBrakePadMm: Float,
    val rearBrakePadMm: Float,
    val batteryStateOfHealthPct: Int,
    val batteryVoltageEst: Float
)

data class VehicleDiagnosticReport(
    val vehicleId: String,
    val currentMileage: Int,
    val overallHealthScore: Int,
    val oemCompliancePct: Int,
    val healthyCount: Int,
    val dueSoonCount: Int,
    val overdueCount: Int,
    val continuousTelemetry: ContinuousWearTelemetry,
    val componentStatuses: List<ComponentHealthStatus>
)

data class DecodedVinProfile(
    val vin: String,
    val year: Int,
    val make: String,
    val model: String,
    val trim: String,
    val engineType: String,
    val drivetrain: DrivetrainType,
    val fuelType: FuelType,
    val decodedBy: String
)

data class VehicleProfile(
    val id: String,
    val vin: String,
    val year: Int,
    val make: String,
    val model: String,
    val trim: String,
    val engineType: String,
    val drivetrain: DrivetrainType,
    val fuelType: FuelType,
    val currentMileage: Int,
    val ownerName: String,
    val passportToken: String,
    val lastServicedByRuleId: Map<String, Int>
)
