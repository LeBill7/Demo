package com.autopulse.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autopulse.app.domain.model.HealthState
import com.autopulse.app.domain.usecase.WorkOrderSubmission
import com.autopulse.app.presentation.components.AnimatedHealthRing
import com.autopulse.app.presentation.components.IsometricVehicleHud
import com.autopulse.app.presentation.components.RollingOdometer
import com.autopulse.app.presentation.viewmodel.AutoPulseIntent
import com.autopulse.app.presentation.viewmodel.AutoPulseUiState

@Composable
fun DriverGarageScreen(
    state: AutoPulseUiState,
    onIntent: (AutoPulseIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicle = state.activeVehicle ?: return
    val diag = state.diagnosticReport ?: return
    var mileageInput by remember(vehicle.currentMileage) {
        mutableStateOf(vehicle.currentMileage.toString())
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "${vehicle.year} ${vehicle.make} ${vehicle.model} • ${vehicle.trim}",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "VIN: ${vehicle.vin} • Drivetrain: ${vehicle.drivetrain}",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RollingOdometer(mileage = vehicle.currentMileage)
                AnimatedHealthRing(
                    remainingLifeFraction = diag.overallHealthScore / 100f,
                    state = if (diag.overdueCount > 0) HealthState.OVERDUE else HealthState.HEALTHY
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = mileageInput,
                    onValueChange = { mileageInput = it },
                    label = { Text("Update Odometer (MI)") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        mileageInput.toIntOrNull()?.let {
                            onIntent(AutoPulseIntent.UpdateOdometer(it))
                        }
                    }
                ) {
                    Text("Recalculate")
                }
            }
        }

        item {
            IsometricVehicleHud(
                statuses = diag.componentStatuses,
                selectedZone = state.selectedHudZone
            )
        }

        items(diag.componentStatuses, key = { it.ruleId }) { item ->
            val badgeColor = when (item.state) {
                HealthState.OVERDUE -> Color(0xFFF43F5E)
                HealthState.DUE_SOON -> Color(0xFFF59E0B)
                HealthState.HEALTHY -> Color(0xFF10B981)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0E1626))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(item.title, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "${(item.wearPercentage * 100).toInt()}% (${item.state.name})",
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = item.safetyDisclaimer,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun MechanicTranslatorScreen(
    state: AutoPulseUiState,
    onIntent: (AutoPulseIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("Blown Head Gasket") }
    var quoteUsd by remember { mutableStateOf("1850") }
    val estimate = state.translatedEstimate

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Mechanic-to-Human Jargon Translator",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Mechanic Term / Quote") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    onIntent(AutoPulseIntent.TranslateJargon(query, quoteUsd.toIntOrNull()))
                }
            ) {
                Text("Decode")
            }
        }

        if (estimate != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0E1626))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(estimate.simpleEnglishTitle, color = Color(0xFF00F2FE), fontWeight = FontWeight.Bold)
                Text("Urgency: ${estimate.urgencyLabel}", color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold)
                Text("What It Does: ${estimate.whatItDoes}", color = Color.White, fontSize = 13.sp)
                Text("Analogy: \"${estimate.analogy}\"", color = Color(0xFF10B981), fontSize = 13.sp)
                Text(
                    text = "Fair Price Range: ${estimate.fairPriceRange.totalFairRangeUsd}",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun PartnerShopTerminalScreen(
    state: AutoPulseUiState,
    onIntent: (AutoPulseIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicle = state.activeVehicle ?: return
    val overdueRules = state.diagnosticReport?.componentStatuses
        ?.filter { it.state != HealthState.HEALTHY }
        ?.map { it.ruleId }
        .orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Partner Shop Tablet Terminal (CameraX QR/VIN Intake)", color = Color.White, fontSize = 18.sp)
        Text("Active Bay Vehicle: ${vehicle.vin} (${vehicle.currentMileage} MI)", color = Color(0xFF00F2FE))
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                onIntent(
                    AutoPulseIntent.SubmitPartnerWorkOrder(
                        WorkOrderSubmission(
                            vehicleId = vehicle.id,
                            shopId = "SHOP-APEX-01",
                            shopName = "Apex Precision Motorsports",
                            intakeOdometerMileage = vehicle.currentMileage + 100,
                            completedRuleIds = overdueRules.ifEmpty { listOf("ENGINE_OIL_SYNTHETIC") },
                            technicianNotes = "Certified multi-point service & cycle reset.",
                            totalInvoiceUsd = 385
                        )
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign & Certify Work Order (Reset Cycles + SHA-256 Seal)")
        }
    }
}

@Composable
fun TrustPassportScreen(
    state: AutoPulseUiState,
    onIntent: (AutoPulseIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val report = useMemoReport(state)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.vehicles.forEach { v ->
                Text(
                    text = "${v.make} ${v.model}",
                    color = Color(0xFF00F2FE),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0E1626))
                        .clickable { onIntent(AutoPulseIntent.SelectVehicle(v.id)) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
        Text(
            text = report,
            color = Color(0xFFE2E8F0),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun useMemoReport(state: AutoPulseUiState): String =
    remember(state.activeVehicle, state.trustPassportReport) {
        TrustPassportPdfExporter.buildBuyerAuditReportDocument(state)
    }
