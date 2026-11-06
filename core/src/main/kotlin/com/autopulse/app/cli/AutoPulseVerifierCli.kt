package com.autopulse.app.cli

import com.autopulse.app.core.logging.StructuredLogger
import com.autopulse.app.core.version.AppVersion
import com.autopulse.app.data.repository.AutoPulseRepository
import com.autopulse.app.domain.engine.MaintenanceCalculationEngine
import com.autopulse.app.domain.engine.MechanicTranslatorEngine
import com.autopulse.app.domain.security.OdometerIntegrityVerifier

fun main() {
    val repository = AutoPulseRepository()
    val maintenanceEngine = MaintenanceCalculationEngine()
    val translatorEngine = MechanicTranslatorEngine()
    val integrityVerifier = OdometerIntegrityVerifier()

    StructuredLogger.info(
        module = "AutoPulseVerifierCli",
        message = "Starting ${AppVersion.APP_NAME} v${AppVersion.VERSION_NAME} diagnostic verification"
    )

    for (vehicle in repository.getAllVehicles()) {
        val diagResult = maintenanceEngine.evaluateFullVehicleDiagnostics(
            vehicleId = vehicle.id,
            currentMileage = vehicle.currentMileage,
            lastServicedByRuleId = vehicle.lastServicedByRuleId
        )
        val ledger = repository.getMileageLedger(vehicle.id)
        val passportResult = integrityVerifier.auditVehicleTrustPassport(vehicle.id, ledger)

        if (diagResult.isSuccess && passportResult.isSuccess) {
            val diag = diagResult.getOrNull()!!
            val passport = passportResult.getOrNull()!!
            StructuredLogger.info(
                module = "AutoPulseVerifierCli",
                message = "Verified ${vehicle.year} ${vehicle.make} ${vehicle.model}",
                context = mapOf(
                    "healthScore" to diag.overallHealthScore,
                    "overdueItems" to diag.overdueCount,
                    "trustScore" to passport.trustScore,
                    "classification" to passport.classification,
                    "rollbackDetected" to passport.rollbackDetected
                )
            )
        }
    }

    val translation = translatorEngine.translateJargon("Blown Head Gasket", 1850)
    if (translation.isSuccess) {
        val item = translation.getOrNull()!!
        StructuredLogger.info(
            module = "AutoPulseVerifierCli",
            message = "Decoded mechanic term '${item.mechanicTerm}' -> ${item.simpleEnglishTitle}",
            context = mapOf("urgency" to item.urgencyLabel, "fairRange" to item.fairPriceRange.totalFairRangeUsd)
        )
    }
}
