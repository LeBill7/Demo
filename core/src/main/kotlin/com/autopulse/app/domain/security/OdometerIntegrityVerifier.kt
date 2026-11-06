package com.autopulse.app.domain.security

import com.autopulse.app.core.logging.StructuredLogger
import com.autopulse.app.core.result.AppError
import com.autopulse.app.core.result.AppResult
import com.autopulse.app.core.result.runCatchingLogged
import java.security.MessageDigest
import java.util.Date

data class MileageLogEntry(
    val id: String,
    val vehicleId: String,
    val mileage: Int,
    val timestamp: Date,
    val source: LogSource,
    val previousHash: String,
    val recordedHash: String = ""
)

enum class LogSource {
    SELF_REPORTED,
    PARTNER_SHOP_CERTIFIED,
    OBD_TELEMATICS,
    STATE_INSPECTION
}

sealed class IntegrityResult {
    data class Valid(val hash: String) : IntegrityResult()
    data class AnomalyDetected(
        val severity: AnomalySeverity,
        val message: String,
        val computedHash: String
    ) : IntegrityResult()
}

enum class AnomalySeverity {
    WARNING,
    CRITICAL_FRAUD
}

data class TrustPassportAuditReport(
    val vehicleId: String,
    val trustScore: Int, // 0 to 100
    val lemonRiskIndex: Int, // 100 - trustScore
    val classification: String,
    val chainIntact: Boolean,
    val rollbackDetected: Boolean,
    val certifiedLogRatioPct: Int,
    val anomalies: List<IntegrityResult.AnomalyDetected>,
    val verifiedEntries: List<MileageLogEntry>
)

class OdometerIntegrityVerifier {

    fun verifyAndHash(
        entry: MileageLogEntry,
        lastVerifiedRecord: MileageLogEntry?
    ): IntegrityResult {
        // 1. Compute SHA-256 Chained Hash
        val payload = "${entry.vehicleId}:${entry.mileage}:${entry.timestamp.time}:${entry.previousHash}"
        val computedHash = sha256(payload)

        if (lastVerifiedRecord == null) {
            return IntegrityResult.Valid(computedHash)
        }

        // 2. Critical Negative Delta Check (Rollback Fraud)
        if (entry.mileage < lastVerifiedRecord.mileage) {
            val rollbackDelta = lastVerifiedRecord.mileage - entry.mileage
            val message = "CRITICAL: Odometer rollback detected! New mileage (${entry.mileage}) is " +
                "$rollbackDelta mi lower than previous verified reading (${lastVerifiedRecord.mileage})."
            StructuredLogger.warn(
                module = "OdometerIntegrityVerifier",
                message = message,
                context = mapOf(
                    "vehicleId" to entry.vehicleId,
                    "previousMileage" to lastVerifiedRecord.mileage,
                    "newMileage" to entry.mileage
                )
            )
            return IntegrityResult.AnomalyDetected(
                severity = AnomalySeverity.CRITICAL_FRAUD,
                message = message,
                computedHash = computedHash
            )
        }

        // 3. Implausible Travel Velocity Check (>800 miles/day)
        val millisElapsed = entry.timestamp.time - lastVerifiedRecord.timestamp.time
        val daysElapsed = maxOf(millisElapsed.toDouble() / (1000.0 * 60.0 * 60.0 * 24.0), 0.001)
        val milesDelta = entry.mileage - lastVerifiedRecord.mileage
        val milesPerDay = milesDelta / daysElapsed

        if (milesPerDay > 800.0 && milesDelta > 1000) {
            val message = "WARNING: Suspicious mileage velocity detected " +
                "(${"%.1f".format(milesPerDay)} mi/day across ${"%.1f".format(daysElapsed)} days)."
            StructuredLogger.warn(
                module = "OdometerIntegrityVerifier",
                message = message,
                context = mapOf(
                    "vehicleId" to entry.vehicleId,
                    "milesPerDay" to "%.1f".format(milesPerDay)
                )
            )
            return IntegrityResult.AnomalyDetected(
                severity = AnomalySeverity.WARNING,
                message = message,
                computedHash = computedHash
            )
        }

        return IntegrityResult.Valid(computedHash)
    }

    /**
     * Evaluates an entire vehicle mileage ledger for hash chain tampering,
     * odometer rollbacks, implausible velocity, and calculates the 0-100 Trust Score.
     */
    fun auditVehicleTrustPassport(
        vehicleId: String,
        entries: List<MileageLogEntry>,
        hasSevereAccidentRecord: Boolean = false
    ): AppResult<TrustPassportAuditReport> = runCatchingLogged(
        module = "OdometerIntegrityVerifier",
        operation = "auditVehicleTrustPassport"
    ) {
        if (vehicleId.isBlank()) {
            throw AppError.ValidationError("Vehicle ID cannot be blank for Trust Passport audit")
        }

        val sorted = entries.sortedBy { it.timestamp.time }
        val anomalies = mutableListOf<IntegrityResult.AnomalyDetected>()
        val verifiedEntries = mutableListOf<MileageLogEntry>()
        var chainIntact = true
        var rollbackDetected = false

        for (index in sorted.indices) {
            val current = sorted[index]
            val prev = if (index == 0) null else verifiedEntries[index - 1]
            val expectedPrevHash = prev?.recordedHash ?: "GENESIS"

            if (current.previousHash != expectedPrevHash) {
                chainIntact = false
                anomalies.add(
                    IntegrityResult.AnomalyDetected(
                        severity = AnomalySeverity.CRITICAL_FRAUD,
                        message = "CRITICAL: Cryptographic previousHash link broken at entry #${index + 1}.",
                        computedHash = computeEntryHash(current)
                    )
                )
            }

            val result = verifyAndHash(current, prev)
            val computedHash = when (result) {
                is IntegrityResult.Valid -> result.hash
                is IntegrityResult.AnomalyDetected -> {
                    if (result.severity == AnomalySeverity.CRITICAL_FRAUD) {
                        rollbackDetected = true
                    }
                    anomalies.add(result)
                    result.computedHash
                }
            }

            if (current.recordedHash.isNotEmpty() && current.recordedHash != computedHash) {
                chainIntact = false
                anomalies.add(
                    IntegrityResult.AnomalyDetected(
                        severity = AnomalySeverity.CRITICAL_FRAUD,
                        message = "CRITICAL: Tampered SHA-256 signature detected at entry #${index + 1}.",
                        computedHash = computedHash
                    )
                )
            }

            verifiedEntries.add(current.copy(recordedHash = computedHash))
        }

        val certifiedCount = sorted.count { it.source != LogSource.SELF_REPORTED }
        val certifiedRatioPct = if (sorted.isNotEmpty()) {
            ((certifiedCount.toDouble() / sorted.size.toDouble()) * 100.0).toInt()
        } else {
            100
        }

        var score = 96 - ((100 - certifiedRatioPct) * 0.14).toInt()
        for (anomaly in anomalies) {
            score -= when (anomaly.severity) {
                AnomalySeverity.CRITICAL_FRAUD -> 65
                AnomalySeverity.WARNING -> 22
            }
        }
        if (hasSevereAccidentRecord) {
            score -= 30
        }

        val trustScore = score.coerceIn(5, 100)
        val classification = when {
            trustScore >= 90 -> "Pristine Verified Record"
            trustScore >= 70 -> "Standard Driver Record"
            trustScore >= 40 -> "Cautionary History"
            else -> "High Risk / Potential Lemon"
        }

        StructuredLogger.info(
            module = "OdometerIntegrityVerifier",
            message = "Completed Trust Passport audit for $vehicleId",
            context = mapOf(
                "trustScore" to trustScore,
                "classification" to classification,
                "rollbackDetected" to rollbackDetected,
                "chainIntact" to chainIntact
            )
        )

        TrustPassportAuditReport(
            vehicleId = vehicleId,
            trustScore = trustScore,
            lemonRiskIndex = 100 - trustScore,
            classification = classification,
            chainIntact = chainIntact,
            rollbackDetected = rollbackDetected,
            certifiedLogRatioPct = certifiedRatioPct,
            anomalies = anomalies,
            verifiedEntries = verifiedEntries
        )
    }

    fun computeEntryHash(entry: MileageLogEntry): String =
        sha256("${entry.vehicleId}:${entry.mileage}:${entry.timestamp.time}:${entry.previousHash}")

    fun generatePartnerServiceSeal(
        vehicleId: String,
        shopId: String,
        mileage: Int,
        completedRuleIds: List<String>,
        timestampMillis: Long
    ): String {
        val canonical = "AUTOPULSE_SEAL:$vehicleId:$shopId:$mileage:${completedRuleIds.sorted().joinToString(",")}:$timestampMillis"
        return sha256(canonical)
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
