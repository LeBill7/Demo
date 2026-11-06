package com.autopulse.app.domain.security

import com.autopulse.app.data.repository.AutoPulseRepository
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OdometerIntegrityVerifierTest {

    private val verifier = OdometerIntegrityVerifier()
    private val repository = AutoPulseRepository(verifier)

    @Test
    fun `verifies intact SHA-256 chain and Pristine Trust Score on clean vehicle`() {
        val ledger = repository.getMileageLedger("VEH-POR-911")
        val auditRes = verifier.auditVehicleTrustPassport("VEH-POR-911", ledger)
        assertTrue(auditRes.isSuccess)
        val report = auditRes.getOrNull()!!
        assertTrue(report.chainIntact)
        assertFalse(report.rollbackDetected)
        assertTrue(report.trustScore >= 90)
        assertEquals("Pristine Verified Record", report.classification)
    }

    @Test
    fun `detects CRITICAL_FRAUD odometer rollback when new mileage is lower than previous`() {
        val ledger = repository.getMileageLedger("VEH-HON-ACC")
        val auditRes = verifier.auditVehicleTrustPassport("VEH-HON-ACC", ledger)
        assertTrue(auditRes.isSuccess)
        val report = auditRes.getOrNull()!!
        assertTrue(report.rollbackDetected)
        assertTrue(report.anomalies.any { it.severity == AnomalySeverity.CRITICAL_FRAUD })
        assertTrue(report.trustScore < 40)
        assertEquals("High Risk / Potential Lemon", report.classification)
    }

    @Test
    fun `flags WARNING for implausible travel velocity exceeding 800 miles per day`() {
        val t1 = Date(1700000000000L)
        val t2 = Date(1700000000000L + 2L * 24L * 3600L * 1000L) // 2 days later
        val first = MileageLogEntry(
            id = "1",
            vehicleId = "VEH-VEL",
            mileage = 10000,
            timestamp = t1,
            source = LogSource.PARTNER_SHOP_CERTIFIED,
            previousHash = "GENESIS"
        )
        val firstHash = verifier.computeEntryHash(first)
        val second = MileageLogEntry(
            id = "2",
            vehicleId = "VEH-VEL",
            mileage = 12500, // +2,500 miles in 2 days = 1,250 mi/day (> 800 mi/day)
            timestamp = t2,
            source = LogSource.SELF_REPORTED,
            previousHash = firstHash
        )

        val result = verifier.verifyAndHash(second, first.copy(recordedHash = firstHash))
        assertTrue(result is IntegrityResult.AnomalyDetected)
        assertEquals(AnomalySeverity.WARNING, result.severity)
    }

    @Test
    fun `detects broken SHA-256 chain when historical mileage record is tampered`() {
        val ledger = repository.getMileageLedger("VEH-POR-911").toMutableList()
        // Tamper with entry #2's mileage without updating hashes
        ledger[1] = ledger[1].copy(mileage = 99999)

        val auditRes = verifier.auditVehicleTrustPassport("VEH-POR-911", ledger)
        assertTrue(auditRes.isSuccess)
        val report = auditRes.getOrNull()!!
        assertFalse(report.chainIntact)
        assertTrue(report.anomalies.any { it.severity == AnomalySeverity.CRITICAL_FRAUD })
    }
}
