package com.autopulse.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autopulse.app.domain.model.HealthState
import com.autopulse.app.domain.usecase.WorkOrderSubmission
import com.autopulse.app.presentation.components.AdaptiveCard
import com.autopulse.app.presentation.components.AdaptiveContainer
import com.autopulse.app.presentation.theme.AutoPulseColors
import com.autopulse.app.presentation.viewmodel.AutoPulseIntent
import com.autopulse.app.presentation.viewmodel.AutoPulseUiState

@Composable
fun PartnerShopTerminalScreen(
    state: AutoPulseUiState,
    onIntent: (AutoPulseIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicle = state.activeVehicle ?: return
    val diag = state.diagnosticReport

    var intakeMileage by remember(vehicle.currentMileage) {
        mutableStateOf((vehicle.currentMileage + 25).toString())
    }
    var invoiceAmount by remember { mutableStateOf("385") }
    var techNotes by remember { mutableStateOf("Multi-point certified inspection & cycle reset.") }

    val completedServices = remember {
        mutableStateListOf(
            "POWERTRAIN_ENGINE_OIL_SYNTHETIC",
            "BRAKING_BRAKE_PADS_FRONT"
        )
    }

    AdaptiveContainer(modifier = modifier) { widthClass ->
        val isTiny = widthClass.isTiny
        val screenPadding = widthClass.screenPadding

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = screenPadding, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Partner Shop Bay Terminal",
                        color = Color.White,
                        fontSize = if (isTiny) 16.sp else 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Apex Precision Motorsports • Bay #02 • QR/VIN Intake",
                        color = AutoPulseColors.AccentViolet,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Step 1: Vehicle Intake
            item {
                AdaptiveCard(isTiny = isTiny) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Step 1: Vehicle Intake",
                            color = AutoPulseColors.PrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${vehicle.year} ${vehicle.make} ${vehicle.model} • VIN: ${vehicle.vin}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        OutlinedTextField(
                            value = intakeMileage,
                            onValueChange = { intakeMileage = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Bay Intake Odometer (MI)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Step 2: Service Checklist
            item {
                AdaptiveCard(isTiny = isTiny) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Step 2: Completed Service Checklist",
                            color = AutoPulseColors.PrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        val candidateRules = listOf(
                            "POWERTRAIN_ENGINE_OIL_SYNTHETIC" to "Synthetic Engine Oil & Filter (+$95)",
                            "BRAKING_BRAKE_PADS_FRONT" to "Front Ceramic Brake Pads (+$180)",
                            "FLUIDS_BRAKE_FLUID_DOT4" to "Brake Fluid DOT 4 Flush (+$110)",
                            "FILTERS_CABIN_AIR_HEPA" to "Cabin HEPA Air Filter (+$45)",
                            "ELECTRICAL_12V_BATTERY_AGM" to "12V AGM Battery Service (+$195)"
                        )

                        candidateRules.forEach { (ruleId, label) ->
                            val isChecked = completedServices.contains(ruleId)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) completedServices.remove(ruleId)
                                        else completedServices.add(ruleId)
                                    }
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        if (it) completedServices.add(ruleId)
                                        else completedServices.remove(ruleId)
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = AutoPulseColors.PrimaryCyan)
                                )
                                Text(
                                    text = label,
                                    color = if (isChecked) Color.White else AutoPulseColors.TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Step 3: Certification & Digital Seal
            item {
                AdaptiveCard(isTiny = isTiny) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Step 3: Certification & Signature",
                            color = AutoPulseColors.PrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        OutlinedTextField(
                            value = invoiceAmount,
                            onValueChange = { invoiceAmount = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Total Invoice ($ USD)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = techNotes,
                            onValueChange = { techNotes = it },
                            label = { Text("Technician Notes") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                val mi = intakeMileage.toIntOrNull() ?: (vehicle.currentMileage + 25)
                                val inv = invoiceAmount.toIntOrNull() ?: 385
                                val rulesToReset = completedServices.ifEmpty { listOf("POWERTRAIN_ENGINE_OIL_SYNTHETIC") }

                                onIntent(
                                    AutoPulseIntent.SubmitPartnerWorkOrder(
                                        WorkOrderSubmission(
                                            vehicleId = vehicle.id,
                                            shopId = "SHOP-APEX-01",
                                            shopName = "Apex Precision Motorsports",
                                            intakeOdometerMileage = mi,
                                            completedRuleIds = rulesToReset,
                                            technicianNotes = techNotes,
                                            totalInvoiceUsd = inv
                                        )
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AutoPulseColors.SuccessEmerald)
                        ) {
                            Text(
                                text = "Sign & Certify Seal (Reset Cycles)",
                                color = Color(0xFF070B14),
                                fontWeight = FontWeight.Black,
                                fontSize = if (isTiny) 11.sp else 13.sp
                            )
                        }
                    }
                }
            }

            // Recent Certified Seals
            val stamps = state.recentServiceStamps
            if (stamps.isNotEmpty()) {
                item {
                    Text(
                        text = "🛡️ Certified Partner Seals (${stamps.size})",
                        color = AutoPulseColors.AccentViolet,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(stamps, key = { it.sha256ServiceSeal }) { stamp ->
                    AdaptiveCard(isTiny = isTiny) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(stamp.shopName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("${stamp.mileageAtService} MI", color = AutoPulseColors.PrimaryCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                            Text(
                                text = "Seal: ${stamp.sha256ServiceSeal.take(16)}...",
                                color = AutoPulseColors.AccentViolet,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "FCM: ${stamp.fcmPushNotificationTitle}",
                                color = AutoPulseColors.TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
