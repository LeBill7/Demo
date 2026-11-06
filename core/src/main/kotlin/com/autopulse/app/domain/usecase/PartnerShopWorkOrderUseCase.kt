package com.autopulse.app.domain.usecase

import com.autopulse.app.core.logging.StructuredLogger
import com.autopulse.app.core.result.AppError
import com.autopulse.app.core.result.AppResult
import com.autopulse.app.core.result.runCatchingLogged
import com.autopulse.app.domain.model.VehicleProfile
import com.autopulse.app.domain.security.LogSource
import com.autopulse.app.domain.security.MileageLogEntry
import com.autopulse.app.domain.security.OdometerIntegrityVerifier
import java.util.Date

data class WorkOrderSubmission(
    val vehicleId: String,
    val shopId: String,
    val shopName: String,
    val intakeOdometerMileage: Int,
    val completedRuleIds: List<String>,
    val technicianNotes: String,
    val totalInvoiceUsd: Int,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class CertifiedServiceStamp(
    val stampId: String,
    val vehicleId: String,
    val shopId: String,
    val shopName: String,
    val mileageAtService: Int,
    val completedRuleIds: List<String>,
    val technicianNotes: String,
    val sha256ServiceSeal: String,
    val fcmPushNotificationTitle: String,
    val fcmPushNotificationBody: String
)

data class WorkOrderExecutionOutcome(
    val updatedVehicle: VehicleProfile,
    val newMileageLedgerEntry: MileageLogEntry,
    val serviceStamp: CertifiedServiceStamp
)

class PartnerShopWorkOrderUseCase(
    private val verifier: OdometerIntegrityVerifier = OdometerIntegrityVerifier()
) {

    fun executeAndCertifyWorkOrder(
        vehicle: VehicleProfile,
        lastMileageEntry: MileageLogEntry?,
        submission: WorkOrderSubmission
    ): AppResult<WorkOrderExecutionOutcome> = runCatchingLogged(
        module = "PartnerShopWorkOrderUseCase",
        operation = "executeAndCertifyWorkOrder"
    ) {
        if (submission.completedRuleIds.isEmpty()) {
            throw AppError.ValidationError("At least one completed maintenance rule must be selected")
        }
        if (submission.shopId.isBlank() || submission.shopName.isBlank()) {
            throw AppError.ValidationError("Certified Partner Shop identity is required")
        }
        if (submission.intakeOdometerMileage < vehicle.currentMileage) {
            throw AppError.ValidationError(
                message = "Partner Shop intake mileage (${submission.intakeOdometerMileage}) cannot be lower than current verified odometer (${vehicle.currentMileage})",
                details = mapOf(
                    "intakeMileage" to submission.intakeOdometerMileage.toString(),
                    "currentMileage" to vehicle.currentMileage.toString()
                )
            )
        }

        // 1. Reset maintenance cycles for all completed rule IDs to the new intake mileage
        val updatedLastServiced = vehicle.lastServicedByRuleId.toMutableMap()
        for (ruleId in submission.completedRuleIds) {
            updatedLastServiced[ruleId] = submission.intakeOdometerMileage
        }

        val updatedVehicle = vehicle.copy(
            currentMileage = submission.intakeOdometerMileage,
            lastServicedByRuleId = updatedLastServiced
        )

        // 2. Append cryptographically chained mileage log entry
        val prevHash = lastMileageEntry?.recordedHash ?: "GENESIS"
        val draftEntry = MileageLogEntry(
            id = "MLOG-${vehicle.id}-${submission.timestampMillis}",
            vehicleId = vehicle.id,
            mileage = submission.intakeOdometerMileage,
            timestamp = Date(submission.timestampMillis),
            source = LogSource.PARTNER_SHOP_CERTIFIED,
            previousHash = prevHash
        )
        val computedHash = verifier.computeEntryHash(draftEntry)
        val signedEntry = draftEntry.copy(recordedHash = computedHash)

        // 3. Issue SHA-256 Partner Service Seal & FCM push payload
        val sealHash = verifier.generatePartnerServiceSeal(
            vehicleId = vehicle.id,
            shopId = submission.shopId,
            mileage = submission.intakeOdometerMileage,
            completedRuleIds = submission.completedRuleIds,
            timestampMillis = submission.timestampMillis
        )

        val stamp = CertifiedServiceStamp(
            stampId = "STAMP-${submission.timestampMillis}",
            vehicleId = vehicle.id,
            shopId = submission.shopId,
            shopName = submission.shopName,
            mileageAtService = submission.intakeOdometerMileage,
            completedRuleIds = submission.completedRuleIds,
            technicianNotes = submission.technicianNotes,
            sha256ServiceSeal = sealHash,
            fcmPushNotificationTitle = "Certified Service Completed by ${submission.shopName}",
            fcmPushNotificationBody = "${submission.completedRuleIds.size} maintenance cycle(s) reset at ${submission.intakeOdometerMileage} mi. SHA-256 Seal: ${sealHash.take(12)}..."
        )

        StructuredLogger.info(
            module = "PartnerShopWorkOrderUseCase",
            message = "Certified work order & reset ${submission.completedRuleIds.size} maintenance cycles",
            context = mapOf(
                "vehicleId" to vehicle.id,
                "shopId" to submission.shopId,
                "mileage" to submission.intakeOdometerMileage,
                "sealPrefix" to sealHash.take(12)
            )
        )

        WorkOrderExecutionOutcome(
            updatedVehicle = updatedVehicle,
            newMileageLedgerEntry = signedEntry,
            serviceStamp = stamp
        )
    }
}
