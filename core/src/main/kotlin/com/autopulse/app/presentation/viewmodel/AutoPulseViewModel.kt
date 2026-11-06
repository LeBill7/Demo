package com.autopulse.app.presentation.viewmodel

import com.autopulse.app.core.logging.StructuredLogger
import com.autopulse.app.core.result.AppResult
import com.autopulse.app.data.repository.AutoPulseRepository
import com.autopulse.app.domain.catalog.CarPartEncyclopediaEntry
import com.autopulse.app.domain.engine.MaintenanceCalculationEngine
import com.autopulse.app.domain.engine.MechanicTranslatorEngine
import com.autopulse.app.domain.engine.TranslatedMechanicEstimate
import com.autopulse.app.domain.model.ComponentCategory
import com.autopulse.app.domain.model.VehicleDiagnosticReport
import com.autopulse.app.domain.model.VehicleProfile
import com.autopulse.app.domain.security.LogSource
import com.autopulse.app.domain.security.MileageLogEntry
import com.autopulse.app.domain.security.OdometerIntegrityVerifier
import com.autopulse.app.domain.security.TrustPassportAuditReport
import com.autopulse.app.domain.usecase.CertifiedServiceStamp
import com.autopulse.app.domain.usecase.PartnerShopWorkOrderUseCase
import com.autopulse.app.domain.usecase.WorkOrderSubmission
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.Date

data class AutoPulseUiState(
    val vehicles: List<VehicleProfile> = emptyList(),
    val activeVehicle: VehicleProfile? = null,
    val diagnosticReport: VehicleDiagnosticReport? = null,
    val trustPassportReport: TrustPassportAuditReport? = null,
    val recentServiceStamps: List<CertifiedServiceStamp> = emptyList(),
    val translatedEstimate: TranslatedMechanicEstimate? = null,
    val encyclopediaEntries: List<CarPartEncyclopediaEntry> = emptyList(),
    val selectedHudZone: String = "ALL",
    val lastErrorBanner: String? = null
)

sealed interface AutoPulseIntent {
    data class SelectVehicle(val vehicleIdOrVin: String) : AutoPulseIntent
    data class UpdateOdometer(val newMileage: Int, val source: LogSource = LogSource.SELF_REPORTED) : AutoPulseIntent
    data class DecodeAndApplyVin(val vin: String) : AutoPulseIntent
    data class TranslateJargon(val term: String, val quotedUsd: Int? = null) : AutoPulseIntent
    data class SearchEncyclopedia(val query: String, val category: ComponentCategory? = null) : AutoPulseIntent
    data class SubmitPartnerWorkOrder(val submission: WorkOrderSubmission) : AutoPulseIntent
}

sealed interface AutoPulseUiEffect {
    data class ShowPushNotification(val title: String, val body: String) : AutoPulseUiEffect
    data class ShowErrorToast(val message: String) : AutoPulseUiEffect
}

class AutoPulseViewModel(
    private val repository: AutoPulseRepository = AutoPulseRepository(),
    private val maintenanceEngine: MaintenanceCalculationEngine = MaintenanceCalculationEngine(),
    private val translatorEngine: MechanicTranslatorEngine = MechanicTranslatorEngine(),
    private val integrityVerifier: OdometerIntegrityVerifier = OdometerIntegrityVerifier(),
    private val shopUseCase: PartnerShopWorkOrderUseCase = PartnerShopWorkOrderUseCase()
) {
    private val _uiState = MutableStateFlow(AutoPulseUiState())
    val uiState: StateFlow<AutoPulseUiState> = _uiState.asStateFlow()

    private val _uiEffects = Channel<AutoPulseUiEffect>(Channel.BUFFERED)
    val uiEffects: Flow<AutoPulseUiEffect> = _uiEffects.receiveAsFlow()

    init {
        val all = repository.getAllVehicles()
        val first = all.firstOrNull()
        val initialTranslation = translatorEngine.translateJargon("Blown Head Gasket", 1850).getOrNull()
        val initialParts = translatorEngine.searchPartsEncyclopedia("").getOrNull().orEmpty()
        if (first != null) {
            refreshStateForVehicle(first, initialTranslation, initialParts)
        }
    }

    fun dispatch(intent: AutoPulseIntent) {
        when (intent) {
            is AutoPulseIntent.SelectVehicle -> handleSelectVehicle(intent.vehicleIdOrVin)
            is AutoPulseIntent.UpdateOdometer -> handleUpdateOdometer(intent.newMileage, intent.source)
            is AutoPulseIntent.DecodeAndApplyVin -> handleDecodeVin(intent.vin)
            is AutoPulseIntent.TranslateJargon -> handleTranslateJargon(intent.term, intent.quotedUsd)
            is AutoPulseIntent.SearchEncyclopedia -> handleSearchEncyclopedia(intent.query, intent.category)
            is AutoPulseIntent.SubmitPartnerWorkOrder -> handleWorkOrder(intent.submission)
        }
    }

    private fun handleSelectVehicle(query: String) {
        when (val result = repository.findVehicleByIdOrVin(query)) {
            is AppResult.Success -> refreshStateForVehicle(result.data)
            is AppResult.Failure -> publishError(result.error.message)
        }
    }

    private fun handleUpdateOdometer(newMileage: Int, source: LogSource) {
        val current = _uiState.value.activeVehicle ?: return
        if (newMileage < 0) {
            publishError("Odometer mileage cannot be negative")
            return
        }
        val ledger = repository.getMileageLedger(current.id)
        val prevHash = ledger.lastOrNull()?.recordedHash ?: "GENESIS"
        val draftEntry = MileageLogEntry(
            id = "MLOG-${current.id}-${ledger.size + 1}",
            vehicleId = current.id,
            mileage = newMileage,
            timestamp = Date(),
            source = source,
            previousHash = prevHash
        )
        val hash = integrityVerifier.computeEntryHash(draftEntry)
        val updatedVehicle = current.copy(currentMileage = newMileage)
        repository.updateVehicleAndAppendMileage(updatedVehicle, draftEntry.copy(recordedHash = hash))
        refreshStateForVehicle(updatedVehicle)
    }

    private fun handleDecodeVin(vin: String) {
        val current = _uiState.value.activeVehicle ?: return
        when (val decodedRes = maintenanceEngine.decodeVin(vin)) {
            is AppResult.Success -> {
                val d = decodedRes.data
                val updated = current.copy(
                    vin = d.vin,
                    year = d.year,
                    make = d.make,
                    model = d.model,
                    trim = d.trim,
                    engineType = d.engineType,
                    drivetrain = d.drivetrain,
                    fuelType = d.fuelType
                )
                refreshStateForVehicle(updated)
            }
            is AppResult.Failure -> publishError(decodedRes.error.message)
        }
    }

    private fun handleTranslateJargon(term: String, quotedUsd: Int?) {
        when (val res = translatorEngine.translateJargon(term, quotedUsd)) {
            is AppResult.Success -> _uiState.value = _uiState.value.copy(
                translatedEstimate = res.data,
                lastErrorBanner = null
            )
            is AppResult.Failure -> publishError(res.error.message)
        }
    }

    private fun handleSearchEncyclopedia(query: String, category: ComponentCategory?) {
        when (val res = translatorEngine.searchPartsEncyclopedia(query, category)) {
            is AppResult.Success -> _uiState.value = _uiState.value.copy(
                encyclopediaEntries = res.data,
                lastErrorBanner = null
            )
            is AppResult.Failure -> publishError(res.error.message)
        }
    }

    private fun handleWorkOrder(submission: WorkOrderSubmission) {
        val current = _uiState.value.activeVehicle ?: return
        val lastEntry = repository.getMileageLedger(current.id).lastOrNull()
        when (val res = shopUseCase.executeAndCertifyWorkOrder(current, lastEntry, submission)) {
            is AppResult.Success -> {
                val outcome = res.data
                repository.updateVehicleAndAppendMileage(
                    updatedVehicle = outcome.updatedVehicle,
                    newEntry = outcome.newMileageLedgerEntry,
                    stamp = outcome.serviceStamp
                )
                refreshStateForVehicle(outcome.updatedVehicle)
                _uiEffects.trySend(
                    AutoPulseUiEffect.ShowPushNotification(
                        title = outcome.serviceStamp.fcmPushNotificationTitle,
                        body = outcome.serviceStamp.fcmPushNotificationBody
                    )
                )
            }
            is AppResult.Failure -> publishError(res.error.message)
        }
    }

    private fun refreshStateForVehicle(
        vehicle: VehicleProfile,
        translationOverride: TranslatedMechanicEstimate? = _uiState.value.translatedEstimate,
        partsOverride: List<CarPartEncyclopediaEntry> = _uiState.value.encyclopediaEntries
    ) {
        val diag = maintenanceEngine.evaluateFullVehicleDiagnostics(
            vehicleId = vehicle.id,
            currentMileage = vehicle.currentMileage,
            lastServicedByRuleId = vehicle.lastServicedByRuleId
        ).getOrNull()

        val ledger = repository.getMileageLedger(vehicle.id)
        val passport = integrityVerifier.auditVehicleTrustPassport(
            vehicleId = vehicle.id,
            entries = ledger
        ).getOrNull()

        StructuredLogger.debug(
            module = "AutoPulseViewModel",
            message = "Refreshed MVI UiState for vehicle ${vehicle.id}"
        )

        _uiState.value = AutoPulseUiState(
            vehicles = repository.getAllVehicles(),
            activeVehicle = vehicle,
            diagnosticReport = diag,
            trustPassportReport = passport,
            recentServiceStamps = repository.getServiceStamps(vehicle.id),
            translatedEstimate = translationOverride,
            encyclopediaEntries = partsOverride,
            lastErrorBanner = null
        )
    }

    private fun publishError(message: String) {
        _uiState.value = _uiState.value.copy(lastErrorBanner = message)
        _uiEffects.trySend(AutoPulseUiEffect.ShowErrorToast(message))
    }
}
