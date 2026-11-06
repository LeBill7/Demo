package com.autopulse.app.presentation.screens

import com.autopulse.app.domain.security.TrustPassportAuditReport
import com.autopulse.app.presentation.viewmodel.AutoPulseIntent
import com.autopulse.app.presentation.viewmodel.AutoPulseUiState

/**
 * Generates a structured Pre-Purchase Buyer Inspection Certificate suitable
 * for Android PdfDocument rendering and Android Share Intent export.
 */
object TrustPassportPdfExporter {

    fun buildBuyerAuditReportDocument(
        state: AutoPulseUiState
    ): String {
        val vehicle = state.activeVehicle ?: return "No active vehicle selected."
        val passport: TrustPassportAuditReport = state.trustPassportReport
            ?: return "Trust Passport audit unavailable."
        val diag = state.diagnosticReport

        return buildString {
            appendLine("================================================================")
            appendLine("         AUTOPULSE IMMUTABLE VEHICLE TRUST PASSPORT             ")
            appendLine("================================================================")
            appendLine("Vehicle:        ${vehicle.year} ${vehicle.make} ${vehicle.model} (${vehicle.trim})")
            appendLine("VIN:            ${vehicle.vin}")
            appendLine("Passport Token: ${vehicle.passportToken}")
            appendLine("Current Miles:  ${vehicle.currentMileage} MI")
            appendLine("----------------------------------------------------------------")
            appendLine("TRUST SCORE:    ${passport.trustScore}/100 (${passport.classification})")
            appendLine("LEMON RISK:     ${passport.lemonRiskIndex}/100")
            appendLine("SHA-256 CHAIN:  ${if (passport.chainIntact) "VERIFIED INTACT" else "TAMPER DETECTED"}")
            appendLine("ROLLBACK FLAG:  ${if (passport.rollbackDetected) "CRITICAL ROLLBACK DETECTED" else "NONE"}")
            if (diag != null) {
                appendLine("OEM COMPLIANCE: ${diag.oemCompliancePct}% (Health Score: ${diag.overallHealthScore}/100)")
            }
            appendLine("----------------------------------------------------------------")
            appendLine("CHRONOLOGICAL SHA-256 ODOMETER LEDGER (${passport.verifiedEntries.size} BLOCKS):")
            for (entry in passport.verifiedEntries) {
                appendLine(
                    " - ${entry.mileage} MI | ${entry.source} | Hash: ${entry.recordedHash.take(16)}..."
                )
            }
            if (passport.anomalies.isNotEmpty()) {
                appendLine("----------------------------------------------------------------")
                appendLine("DETECTED FRAUD / VELOCITY ANOMALIES:")
                for (alert in passport.anomalies) {
                    appendLine(" [${alert.severity}] ${alert.message}")
                }
            }
            appendLine("================================================================")
        }
    }
}
