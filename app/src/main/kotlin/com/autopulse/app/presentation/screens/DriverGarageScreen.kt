package com.autopulse.app.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autopulse.app.domain.model.ComponentCategory
import com.autopulse.app.domain.model.ComponentHealthStatus
import com.autopulse.app.domain.model.HealthState
import com.autopulse.app.presentation.components.AdaptiveCard
import com.autopulse.app.presentation.components.AdaptiveContainer
import com.autopulse.app.presentation.components.AnimatedHealthRing
import com.autopulse.app.presentation.components.IsometricVehicleHud
import com.autopulse.app.presentation.components.OdometerUpdateModal
import com.autopulse.app.presentation.components.RollingOdometer
import com.autopulse.app.presentation.components.StatusPill
import com.autopulse.app.presentation.components.WindowWidthClass
import com.autopulse.app.presentation.theme.AutoPulseColors
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

    var selectedCategory by remember { mutableStateOf<ComponentCategory?>(null) }
    var selectedZone by remember { mutableStateOf("ALL") }
    var showVehiclePicker by remember { mutableStateOf(false) }
    var showOdometerDialog by remember { mutableStateOf(false) }
    var expandHealthyItems by remember { mutableStateOf(false) }

    AdaptiveContainer(modifier = modifier) { widthClass ->
        val isTiny = widthClass.isTiny
        val screenPadding = widthClass.screenPadding

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = screenPadding, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Vehicle Identity Header with Quick Switcher
            item {
                AdaptiveCard(isTiny = isTiny) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${vehicle.year} ${vehicle.make} ${vehicle.model}",
                                color = AutoPulseColors.TextPrimary,
                                fontSize = if (isTiny) 15.sp else 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AutoPulseColors.PrimaryCyan.copy(alpha = 0.15f))
                                    .clickable { showVehiclePicker = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Switch",
                                    color = AutoPulseColors.PrimaryCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "${vehicle.trim} • VIN: ${vehicle.vin.take(8)}... • ${vehicle.drivetrain}",
                            color = AutoPulseColors.TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // 2. Hero Action Guidance Banner
            item {
                val (bannerBg, borderCol, title, subtitle) = when {
                    diag.overdueCount > 0 -> Quadruple(
                        AutoPulseColors.CriticalCrimson.copy(alpha = 0.15f),
                        AutoPulseColors.CriticalCrimson,
                        "🚨 Immediate Action Required",
                        "${diag.overdueCount} subsystem(s) are critically overdue. Review below."
                    )
                    diag.dueSoonCount > 0 -> Quadruple(
                        AutoPulseColors.WarningAmber.copy(alpha = 0.15f),
                        AutoPulseColors.WarningAmber,
                        "⚠️ Upcoming Maintenance",
                        "${diag.dueSoonCount} component(s) need attention in the next 1,000 MI."
                    )
                    else -> Quadruple(
                        AutoPulseColors.SuccessEmerald.copy(alpha = 0.12f),
                        AutoPulseColors.SuccessEmerald,
                        "✅ All Systems Optimal",
                        "Vehicle health score is ${diag.overallHealthScore}%. Safe to drive."
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(bannerBg)
                        .border(1.dp, borderCol, RoundedCornerShape(10.dp))
                        .padding(if (isTiny) 8.dp else 12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = title, color = borderCol, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = subtitle, color = AutoPulseColors.TextSecondary, fontSize = 11.sp)
                    }
                }
            }

            // 3. Telemetry Block (Rolling Odometer + Health Ring)
            item {
                AdaptiveCard(isTiny = isTiny) {
                    if (isTiny) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RollingOdometer(
                                mileage = vehicle.currentMileage,
                                isTiny = true,
                                onClick = { showOdometerDialog = true }
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AnimatedHealthRing(
                                    remainingLifeFraction = diag.overallHealthScore / 100f,
                                    state = if (diag.overdueCount > 0) HealthState.OVERDUE else HealthState.HEALTHY,
                                    ringSize = 48.dp,
                                    strokeWidth = 5.dp
                                )
                                Column {
                                    Text(
                                        text = "${diag.overallHealthScore}%",
                                        color = AutoPulseColors.PrimaryCyan,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text("Overall Health", color = AutoPulseColors.TextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Recorded Odometer (Tap to Update)", color = AutoPulseColors.TextSecondary, fontSize = 11.sp)
                                RollingOdometer(
                                    mileage = vehicle.currentMileage,
                                    onClick = { showOdometerDialog = true }
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AnimatedHealthRing(
                                    remainingLifeFraction = diag.overallHealthScore / 100f,
                                    state = if (diag.overdueCount > 0) HealthState.OVERDUE else HealthState.HEALTHY,
                                    ringSize = 56.dp,
                                    strokeWidth = 6.dp
                                )
                                Text(
                                    text = "${diag.overallHealthScore}%",
                                    color = AutoPulseColors.PrimaryCyan,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // 4. Interactive 3D Wireframe HUD
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ALL", "ENGINE", "BRAKES", "CABIN").forEach { zone ->
                            val isSelected = selectedZone == zone
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) AutoPulseColors.PrimaryCyan else AutoPulseColors.SurfaceDark)
                                    .clickable { selectedZone = zone }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = zone,
                                    color = if (isSelected) Color(0xFF070B14) else AutoPulseColors.TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    IsometricVehicleHud(
                        statuses = diag.componentStatuses,
                        selectedZone = selectedZone,
                        isTiny = isTiny
                    )
                }
            }

            // 5. Category Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val allSelected = selectedCategory == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (allSelected) AutoPulseColors.PrimaryCyan else AutoPulseColors.SurfaceDark)
                            .clickable { selectedCategory = null }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "All (42)",
                            color = if (allSelected) Color(0xFF070B14) else AutoPulseColors.TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    ComponentCategory.entries.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        val count = diag.componentStatuses.count { it.ruleId.startsWith(cat.name) }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) AutoPulseColors.PrimaryCyan else AutoPulseColors.SurfaceDark)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${cat.name.lowercase().replaceFirstChar { it.uppercase() }} ($count)",
                                color = if (isSelected) Color(0xFF070B14) else AutoPulseColors.TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 6. Filtered Components (Progressive Disclosure)
            val filtered = diag.componentStatuses.filter { item ->
                selectedCategory == null || item.ruleId.startsWith(selectedCategory!!.name)
            }
            val overdueItems = filtered.filter { it.state == HealthState.OVERDUE }
            val dueSoonItems = filtered.filter { it.state == HealthState.DUE_SOON }
            val healthyItems = filtered.filter { it.state == HealthState.HEALTHY }

            if (overdueItems.isNotEmpty()) {
                item {
                    Text(
                        text = "🚨 Critical Overdue (${overdueItems.size})",
                        color = AutoPulseColors.CriticalCrimson,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(overdueItems, key = { it.ruleId }) { item ->
                    ComponentCard(item = item, isTiny = isTiny)
                }
            }

            if (dueSoonItems.isNotEmpty()) {
                item {
                    Text(
                        text = "⚠️ Due Soon (${dueSoonItems.size})",
                        color = AutoPulseColors.WarningAmber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(dueSoonItems, key = { it.ruleId }) { item ->
                    ComponentCard(item = item, isTiny = isTiny)
                }
            }

            if (healthyItems.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AutoPulseColors.SurfaceDark)
                            .clickable { expandHealthyItems = !expandHealthyItems }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✅ Healthy Subsystems (${healthyItems.size})",
                                color = AutoPulseColors.SuccessEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (expandHealthyItems) "Hide ▲" else "Show ▼",
                                color = AutoPulseColors.TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                if (expandHealthyItems) {
                    items(healthyItems, key = { it.ruleId }) { item ->
                        ComponentCard(item = item, isTiny = isTiny)
                    }
                }
            }
        }

        // Dialogs
        if (showVehiclePicker) {
            AlertDialog(
                onDismissRequest = { showVehiclePicker = false },
                title = { Text("Select Garage Vehicle", color = Color.White) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.vehicles.forEach { v ->
                            val isSelected = v.id == vehicle.id
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AutoPulseColors.PrimaryCyan.copy(alpha = 0.2f) else AutoPulseColors.SurfaceDark)
                                    .clickable {
                                        onIntent(AutoPulseIntent.SelectVehicle(v.id))
                                        showVehiclePicker = false
                                    }
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "${v.year} ${v.make} ${v.model} (${v.currentMileage} MI)",
                                    color = if (isSelected) AutoPulseColors.PrimaryCyan else Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showVehiclePicker = false }) {
                        Text("Close", color = AutoPulseColors.PrimaryCyan)
                    }
                },
                containerColor = AutoPulseColors.SurfaceDark
            )
        }

        if (showOdometerDialog) {
            OdometerUpdateModal(
                currentMileage = vehicle.currentMileage,
                onDismiss = { showOdometerDialog = false },
                onConfirm = { newMi ->
                    onIntent(AutoPulseIntent.UpdateOdometer(newMi))
                    showOdometerDialog = false
                }
            )
        }
    }
}

@Composable
private fun ComponentCard(item: ComponentHealthStatus, isTiny: Boolean) {
    AdaptiveCard(isTiny = isTiny) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = if (isTiny) 12.sp else 13.sp,
                    modifier = Modifier.weight(1f)
                )
                StatusPill(
                    label = "${(item.wearPercentage * 100).toInt()}%",
                    state = item.state
                )
            }
            Text(
                text = "Remaining: ${item.remainingMiles} MI • ${item.state.name}",
                color = AutoPulseColors.TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = item.safetyDisclaimer,
                color = AutoPulseColors.TextTertiary,
                fontSize = 10.sp
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
