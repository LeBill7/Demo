package com.autopulse.app.presentation.viewmodel

import com.autopulse.app.core.logging.StructuredLogger
import com.autopulse.app.domain.usecase.WorkOrderSubmission
import com.autopulse.app.presentation.screens.TrustPassportPdfExporter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ViewModelAndShopIntegrationTest {

    @Test
    fun `executes Partner Shop work order, resets wear cycles, and issues SHA-256 seal`() {
        val vm = AutoPulseViewModel()
        val initialVehicle = vm.uiState.value.activeVehicle!!
        assertEquals("VEH-POR-911", initialVehicle.id)

        vm.dispatch(
            AutoPulseIntent.SubmitPartnerWorkOrder(
                WorkOrderSubmission(
                    vehicleId = initialVehicle.id,
                    shopId = "SHOP-APEX-01",
                    shopName = "Apex Precision Motorsports",
                    intakeOdometerMileage = 45000,
                    completedRuleIds = listOf("ENGINE_OIL_SYNTHETIC", "FRONT_BRAKE_PADS"),
                    technicianNotes = "Synthetic oil & ceramic front pads installed.",
                    totalInvoiceUsd = 390
                )
            )
        )

        val updatedState = vm.uiState.value
        assertEquals(45000, updatedState.activeVehicle?.currentMileage)
        assertEquals(
            45000,
            updatedState.activeVehicle?.lastServicedByRuleId?.get("FRONT_BRAKE_PADS")
        )
        assertTrue(updatedState.recentServiceStamps.isNotEmpty())
        assertEquals(64, updatedState.recentServiceStamps.first().sha256ServiceSeal.length)

        val exportedReport = TrustPassportPdfExporter.buildBuyerAuditReportDocument(updatedState)
        assertTrue(exportedReport.contains("AUTOPULSE IMMUTABLE VEHICLE TRUST PASSPORT"))
        assertTrue(exportedReport.contains("45000 MI"))
    }

    @Test
    fun `redacts sensitive keys in StructuredLogger`() {
        val entry = StructuredLogger.info(
            module = "SecurityAuditTest",
            message = "Checking redaction",
            context = mapOf(
                "vehicleId" to "VEH-POR-911",
                "apiKey" to "secret-key-12345",
                "authToken" to "bearer-xyz",
                "password" to "hunter2"
            )
        )
        assertEquals("VEH-POR-911", entry.context["vehicleId"])
        assertEquals("[REDACTED]", entry.context["apiKey"])
        assertEquals("[REDACTED]", entry.context["authToken"])
        assertEquals("[REDACTED]", entry.context["password"])
    }
}
