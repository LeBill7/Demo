package com.autopulse.app.data.repository

import com.autopulse.app.core.result.AppError
import com.autopulse.app.core.result.AppResult
import com.autopulse.app.core.result.runCatchingLogged
import com.autopulse.app.domain.model.DrivetrainType
import com.autopulse.app.domain.model.FuelType
import com.autopulse.app.domain.model.VehicleProfile
import com.autopulse.app.domain.security.LogSource
import com.autopulse.app.domain.security.MileageLogEntry
import com.autopulse.app.domain.security.OdometerIntegrityVerifier
import com.autopulse.app.domain.usecase.CertifiedServiceStamp
import java.util.Date

class AutoPulseRepository(
    private val verifier: OdometerIntegrityVerifier = OdometerIntegrityVerifier()
) {
    private val vehicles = LinkedHashMap<String, VehicleProfile>()
    private val mileageLedgers = LinkedHashMap<String, MutableList<MileageLogEntry>>()
    private val serviceStamps = LinkedHashMap<String, MutableList<CertifiedServiceStamp>>()

    init {
        seedInitialEcosystemData()
    }

    fun getAllVehicles(): List<VehicleProfile> = vehicles.values.toList()

    fun findVehicleByIdOrVin(query: String): AppResult<VehicleProfile> = runCatchingLogged(
        module = "AutoPulseRepository",
        operation = "findVehicleByIdOrVin"
    ) {
        val cleaned = query.trim().uppercase()
        vehicles.values.find {
            it.id.uppercase() == cleaned ||
                it.vin.uppercase() == cleaned ||
                it.passportToken.uppercase() == cleaned
        } ?: throw AppError.NotFoundError(
            message = "No vehicle found matching ID, VIN, or QR token: $query",
            details = mapOf("query" to query)
        )
    }

    fun getMileageLedger(vehicleId: String): List<MileageLogEntry> =
        mileageLedgers[vehicleId]?.toList() ?: emptyList()

    fun getServiceStamps(vehicleId: String): List<CertifiedServiceStamp> =
        serviceStamps[vehicleId]?.toList() ?: emptyList()

    fun updateVehicleAndAppendMileage(
        updatedVehicle: VehicleProfile,
        newEntry: MileageLogEntry,
        stamp: CertifiedServiceStamp? = null
    ): AppResult<VehicleProfile> = runCatchingLogged(
        module = "AutoPulseRepository",
        operation = "updateVehicleAndAppendMileage"
    ) {
        vehicles[updatedVehicle.id] = updatedVehicle
        val ledger = mileageLedgers.getOrPut(updatedVehicle.id) { mutableListOf() }
        ledger.add(newEntry)
        if (stamp != null) {
            val stamps = serviceStamps.getOrPut(updatedVehicle.id) { mutableListOf() }
            stamps.add(0, stamp)
        }
        updatedVehicle
    }

    private fun seedInitialEcosystemData() {
        val v1Id = "VEH-POR-911"
        val porsche = VehicleProfile(
            id = v1Id,
            vin = "WP0AB2A99PS224911",
            year = 2023,
            make = "Porsche",
            model = "911 Carrera 4S",
            trim = "992 AWD Coupe",
            engineType = "3.0L Twin-Turbo Flat-6",
            drivetrain = DrivetrainType.AWD,
            fuelType = FuelType.GASOLINE,
            currentMileage = 44750,
            ownerName = "Alex Rivera",
            passportToken = "QR-PASSPORT-911-VERIFIED",
            lastServicedByRuleId = mapOf(
                "ENGINE_OIL_SYNTHETIC" to 39800, // 4,950 mi ago -> 99% DUE_SOON
                "FRONT_BRAKE_PADS" to 8500,      // 36,250 mi ago -> 103% OVERDUE
                "TIRE_ROTATION_BALANCE" to 35000, // 9,750 mi ago -> 97.5% DUE_SOON
                "CABIN_HEPA_AIR_FILTER" to 31000, // 13,750 mi ago -> 114% OVERDUE
                "BRAKE_FLUID_DOT4" to 36800      // 7,950 mi ago -> 26.5% HEALTHY
            )
        )
        vehicles[v1Id] = porsche
        mileageLedgers[v1Id] = buildChainedEntries(
            vehicleId = v1Id,
            points = listOf(
                Triple(12400, 1718445600000L, LogSource.PARTNER_SHOP_CERTIFIED),
                Triple(24900, 1741617000000L, LogSource.PARTNER_SHOP_CERTIFIED),
                Triple(36800, 1768900500000L, LogSource.OBD_TELEMATICS),
                Triple(44750, 1790873100000L, LogSource.PARTNER_SHOP_CERTIFIED)
            )
        )

        val v2Id = "VEH-HON-ACC"
        val accord = VehicleProfile(
            id = v2Id,
            vin = "1HGCV2F34NA088412",
            year = 2022,
            make = "Honda",
            model = "Accord",
            trim = "Sport 2.0T",
            engineType = "2.0L VTEC Turbo I4",
            drivetrain = DrivetrainType.FWD,
            fuelType = FuelType.GASOLINE,
            currentMileage = 42100,
            ownerName = "Pre-Purchase Inspection Lot #44",
            passportToken = "QR-PASSPORT-ACCORD-ROLLBACK",
            lastServicedByRuleId = mapOf(
                "ENGINE_OIL_SYNTHETIC" to 35000,
                "FRONT_BRAKE_PADS" to 5000
            )
        )
        vehicles[v2Id] = accord
        mileageLedgers[v2Id] = buildChainedEntries(
            vehicleId = v2Id,
            points = listOf(
                Triple(28000, 1714557600000L, LogSource.PARTNER_SHOP_CERTIFIED),
                Triple(61500, 1755000000000L, LogSource.SELF_REPORTED),
                Triple(42100, 1789743600000L, LogSource.SELF_REPORTED) // Intentional Odometer Rollback!
            )
        )
    }

    private fun buildChainedEntries(
        vehicleId: String,
        points: List<Triple<Int, Long, LogSource>>
    ): MutableList<MileageLogEntry> {
        val list = mutableListOf<MileageLogEntry>()
        var prevHash = "GENESIS"
        points.forEachIndexed { idx, (mileage, millis, source) ->
            val raw = MileageLogEntry(
                id = "MLOG-$vehicleId-${idx + 1}",
                vehicleId = vehicleId,
                mileage = mileage,
                timestamp = Date(millis),
                source = source,
                previousHash = prevHash
            )
            val hash = verifier.computeEntryHash(raw)
            list.add(raw.copy(recordedHash = hash))
            prevHash = hash
        }
        return list
    }
}
