package com.autopulse.app.presentation.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autopulse.app.domain.model.HealthState
import com.autopulse.app.presentation.components.AdaptiveCard
import com.autopulse.app.presentation.components.AdaptiveContainer
import com.autopulse.app.presentation.components.AnimatedHealthRing
import com.autopulse.app.presentation.theme.AutoPulseColors
import com.autopulse.app.presentation.viewmodel.AutoPulseIntent
import com.autopulse.app.presentation.viewmodel.AutoPulseUiState

@Composable
fun TrustPassportScreen(
    state: AutoPulseUiState,
    onIntent: (AutoPulseIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicle = state.activeVehicle ?: return
    val passport = state.trustPassportReport
    var showFullAuditReport by remember { mutableStateOf(false) }

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
                        text = "Vehicle Trust Passport",
                        color = Color.White,
                        fontSize = if (isTiny) 16.sp else 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Immutable SHA-256 Ledger & Anti-Fraud Shield",
                        color = AutoPulseColors.PrimaryCyan,
                        fontSize = 11.sp
                    )
                }
            }

            // Vehicle Selector Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    state.vehicles.forEach { v ->
                        val isSelected = v.id == vehicle.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) AutoPulseColors.PrimaryCyan else AutoPulseColors.SurfaceDark)
                                .clickable { onIntent(AutoPulseIntent.SelectVehicle(v.id)) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${v.make} ${v.model}",
                                color = if (isSelected) Color(0xFF070B14) else AutoPulseColors.TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Trust Score Gauge Card
            if (passport != null) {
                item {
                    val (riskBg, riskText, riskColor) = when {
                        passport.rollbackDetected -> Triple(
                            AutoPulseColors.CriticalCrimson.copy(alpha = 0.25f),
                            "CRITICAL ODOMETER FRAUD DETECTED",
                            AutoPulseColors.CriticalCrimson
                        )
                        passport.lemonRiskIndex >= 50 -> Triple(
                            AutoPulseColors.CriticalCrimson.copy(alpha = 0.15f),
                            "HIGH LEMON RISK (${passport.classification})",
                            AutoPulseColors.CriticalCrimson
                        )
                        passport.lemonRiskIndex >= 25 -> Triple(
                            AutoPulseColors.WarningAmber.copy(alpha = 0.15f),
                            "MODERATE RISK (${passport.classification})",
                            AutoPulseColors.WarningAmber
                        )
                        else -> Triple(
                            AutoPulseColors.SuccessEmerald.copy(alpha = 0.15f),
                            "LOW LEMON RISK — PRISTINE (${passport.classification})",
                            AutoPulseColors.SuccessEmerald
                        )
                    }

                    AdaptiveCard(isTiny = isTiny) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${passport.trustScore}/100",
                                        color = AutoPulseColors.PrimaryCyan,
                                        fontSize = if (isTiny) 22.sp else 28.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text("Trust & Integrity Score", color = AutoPulseColors.TextSecondary, fontSize = 11.sp)
                                }
                                AnimatedHealthRing(
                                    remainingLifeFraction = passport.trustScore / 100f,
                                    state = if (passport.rollbackDetected || !passport.chainIntact) HealthState.OVERDUE else HealthState.HEALTHY,
                                    ringSize = if (isTiny) 48.dp else 56.dp,
                                    strokeWidth = 6.dp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(riskBg)
                                    .border(1.dp, riskColor, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = riskText,
                                    color = riskColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Anti-Fraud Checklist
                item {
                    AdaptiveCard(isTiny = isTiny) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "🛡️ Anti-Fraud Shield Verification",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            ChecklistItem(
                                label = "SHA-256 Ledger Integrity",
                                isPassing = passport.chainIntact,
                                detail = "${passport.verifiedEntries.size} entries verified cryptographically"
                            )
                            ChecklistItem(
                                label = "Rollback Fraud Check",
                                isPassing = !passport.rollbackDetected,
                                detail = if (passport.rollbackDetected) "Negative delta detected!" else "Zero rollbacks detected (ΔM ≥ 0)"
                            )
                            ChecklistItem(
                                label = "Certified Log Ratio",
                                isPassing = passport.certifiedLogRatioPct >= 40,
                                detail = "${passport.certifiedLogRatioPct}% partner certified entries"
                            )
                        }
                    }
                }

                // Buyer Recommendation Card
                item {
                    val recommendation = when {
                        passport.rollbackDetected || !passport.chainIntact -> "DO NOT PURCHASE — Severe ledger tampering detected."
                        passport.trustScore >= 80 -> "BUY WITH CONFIDENCE — Authentic verified history."
                        else -> "PROCEED WITH CAUTION — Multi-point mechanical inspection advised."
                    }
                    AdaptiveCard(isTiny = isTiny) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Buyer Recommendation: $recommendation",
                                color = AutoPulseColors.PrimaryCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Verified Log Entries: ${passport.verifiedEntries.size} • Lemon Risk: ${passport.lemonRiskIndex}/100",
                                color = AutoPulseColors.TextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Button(
                                onClick = { showFullAuditReport = true },
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AutoPulseColors.SurfaceBorder)
                            ) {
                                Text("Export Full Inspection Audit Report", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Full Report Dialog
        if (showFullAuditReport) {
            val reportText = remember(state) {
                TrustPassportPdfExporter.buildBuyerAuditReportDocument(state)
            }
            AlertDialog(
                onDismissRequest = { showFullAuditReport = false },
                title = { Text("Pre-Purchase Buyer Audit", color = Color.White) },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth().height(320.dp).padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = reportText,
                            color = AutoPulseColors.TextPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFullAuditReport = false }) {
                        Text("Close", color = AutoPulseColors.PrimaryCyan)
                    }
                },
                containerColor = AutoPulseColors.SurfaceDark
            )
        }
    }
}

@Composable
private fun ChecklistItem(label: String, isPassing: Boolean, detail: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(detail, color = AutoPulseColors.TextSecondary, fontSize = 10.sp)
        }
        Text(
            text = if (isPassing) "PASS ✓" else "FAIL ✗",
            color = if (isPassing) AutoPulseColors.SuccessEmerald else AutoPulseColors.CriticalCrimson,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
