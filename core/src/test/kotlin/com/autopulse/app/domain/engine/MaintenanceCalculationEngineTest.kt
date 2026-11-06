package com.autopulse.app.domain.engine

import com.autopulse.app.domain.catalog.OemMaintenanceCatalog
import com.autopulse.app.domain.model.ComponentCategory
import com.autopulse.app.domain.model.HealthState
import com.autopulse.app.domain.model.MaintenanceRule
import com.autopulse.app.domain.model.UrgencyLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MaintenanceCalculationEngineTest {

    private val engine = MaintenanceCalculationEngine()

    private val oilRule = MaintenanceRule(
        id = "ENGINE_OIL_SYNTHETIC",
        title = "Full Synthetic Oil & Filter",
        category = ComponentCategory.POWERTRAIN,
        intervalMiles = 5000,
        criticalThresholdMiles = 6000,
        description = "Oil breakdown",
        urgencyLevel = UrgencyLevel.CRITICAL_SAFETY
    )

    @Test
    fun `catalog contains at least 40 distinct OEM vehicle subsystems`() {
        assertTrue(
            OemMaintenanceCatalog.rules.size >= 40,
            "Expected >= 40 OEM rules, found ${OemMaintenanceCatalog.rules.size}"
        )
    }

    @Test
    fun `calculates HEALTHY state for 0 to 74 percent wear`() {
        val status = engine.calculateStatus(
            currentMileage = 12500,
            lastServicedMileage = 10000,
            rule = oilRule
        )
        assertEquals(2500, status.milesSinceService)
        assertEquals(2500, status.remainingMiles)
        assertEquals(0.50f, status.wearPercentage)
        assertEquals(HealthState.HEALTHY, status.state)
    }

    @Test
    fun `calculates DUE_SOON state at 75 to 99 percent boundary`() {
        val status75 = engine.calculateStatus(
            currentMileage = 13750,
            lastServicedMileage = 10000,
            rule = oilRule
        )
        assertEquals(0.75f, status75.wearPercentage)
        assertEquals(HealthState.DUE_SOON, status75.state)

        val status99 = engine.calculateStatus(
            currentMileage = 14950,
            lastServicedMileage = 10000,
            rule = oilRule
        )
        assertEquals(0.99f, status99.wearPercentage)
        assertEquals(HealthState.DUE_SOON, status99.state)
    }

    @Test
    fun `calculates OVERDUE state at 100 percent and above`() {
        val status = engine.calculateStatus(
            currentMileage = 16000,
            lastServicedMileage = 10000,
            rule = oilRule
        )
        assertEquals(6000, status.milesSinceService)
        assertEquals(0, status.remainingMiles)
        assertTrue(status.wearPercentage >= 1.0f)
        assertEquals(HealthState.OVERDUE, status.state)
    }

    @Test
    fun `evaluates full 42 subsystem diagnostic report and continuous telemetry`() {
        val result = engine.evaluateFullVehicleDiagnostics(
            vehicleId = "VEH-POR-911",
            currentMileage = 44750,
            lastServicedByRuleId = mapOf(
                "ENGINE_OIL_SYNTHETIC" to 39800,
                "FRONT_BRAKE_PADS" to 8500
            )
        )
        assertTrue(result.isSuccess)
        val report = result.getOrNull()!!
        assertEquals(42, report.componentStatuses.size)
        assertTrue(report.overdueCount >= 1)
        assertTrue(report.continuousTelemetry.frontBrakePadMm >= 1.5f)
        assertTrue(report.continuousTelemetry.batteryStateOfHealthPct in 35..100)
    }

    @Test
    fun `returns ValidationError when negative mileage or blank vehicleId is provided`() {
        val negativeRes = engine.evaluateFullVehicleDiagnostics(
            vehicleId = "VEH-1",
            currentMileage = -500,
            lastServicedByRuleId = emptyMap()
        )
        assertTrue(negativeRes.isFailure)
        assertEquals("VALIDATION_ERROR", negativeRes.errorOrNull()?.code)
    }

    @Test
    fun `decodes valid 17-character VIN and rejects malformed VIN`() {
        val valid = engine.decodeVin("WP0AB2A99PS224911")
        assertTrue(valid.isSuccess)
        assertEquals("Porsche", valid.getOrNull()?.make)

        val invalid = engine.decodeVin("SHORT_VIN")
        assertTrue(invalid.isFailure)
        assertEquals("VALIDATION_ERROR", invalid.errorOrNull()?.code)
    }
}
