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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.autopulse.app.domain.engine.PriceFairnessVerdict
import com.autopulse.app.domain.model.ComponentCategory
import com.autopulse.app.presentation.components.AdaptiveCard
import com.autopulse.app.presentation.components.AdaptiveContainer
import com.autopulse.app.presentation.theme.AutoPulseColors
import com.autopulse.app.presentation.viewmodel.AutoPulseIntent
import com.autopulse.app.presentation.viewmodel.AutoPulseUiState

@Composable
fun MechanicTranslatorScreen(
    state: AutoPulseUiState,
    onIntent: (AutoPulseIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("Blown Head Gasket") }
    var quoteUsd by remember { mutableStateOf("1850") }
    var showEncyclopedia by remember { mutableStateOf(false) }
    var partSearchQuery by remember { mutableStateOf("") }
    val checkedQuestions = remember { mutableStateListOf<Int>() }

    val presetQuotes = listOf(
        "Blown Head Gasket",
        "Differential Pinion Seal",
        "Tie Rod End Play",
        "Catalytic Converter P0420",
        "Brake Caliper Seized",
        "Timing Belt Replacement"
    )

    val estimate = state.translatedEstimate

    AdaptiveContainer(modifier = modifier) { widthClass ->
        val isTiny = widthClass.isTiny
        val screenPadding = widthClass.screenPadding

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = screenPadding, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Mechanic Copilot",
                        color = Color.White,
                        fontSize = if (isTiny) 16.sp else 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Decode technical jargon, verify fair pricing, and prepare questions.",
                        color = AutoPulseColors.TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // 2. Preset Quick-Pick Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetQuotes.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(AutoPulseColors.SurfaceDark)
                                .border(1.dp, AutoPulseColors.SurfaceBorder, RoundedCornerShape(16.dp))
                                .clickable {
                                    query = preset
                                    checkedQuestions.clear()
                                    onIntent(AutoPulseIntent.TranslateJargon(preset, quoteUsd.toIntOrNull()))
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(preset, color = AutoPulseColors.PrimaryCyan, fontSize = 11.sp)
                        }
                    }
                }
            }

            // 3. Query & Quote Inputs
            item {
                AdaptiveCard(isTiny = isTiny) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            label = { Text("Mechanic Diagnosis / Term") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = quoteUsd,
                            onValueChange = { quoteUsd = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Shop Quote Amount ($ USD)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                checkedQuestions.clear()
                                onIntent(AutoPulseIntent.TranslateJargon(query, quoteUsd.toIntOrNull()))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AutoPulseColors.PrimaryCyan)
                        ) {
                            Text("Decode & Audit Quote", color = Color(0xFF070B14), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 4. Translated Results
            if (estimate != null) {
                // Fairness Meter
                item {
                    val eval = estimate.quoteEvaluation
                    val (verdictColor, verdictText) = when (eval?.verdict) {
                        PriceFairnessVerdict.FAIR_MARKET_PRICE -> AutoPulseColors.SuccessEmerald to "✅ Fair Market Price"
                        PriceFairnessVerdict.SLIGHTLY_ABOVE_AVERAGE -> AutoPulseColors.WarningAmber to "⚖️ Acceptable Estimate"
                        PriceFairnessVerdict.OVERPRICED_SECOND_OPINION_RECOMMENDED -> AutoPulseColors.CriticalCrimson to "🚨 Overpriced Quote"
                        PriceFairnessVerdict.BELOW_MARKET_VERIFY_OEM_PARTS -> AutoPulseColors.WarningAmber to "⚠️ Below Market (Verify Parts)"
                        null -> AutoPulseColors.TextSecondary to "Fair Market Range"
                    }

                    AdaptiveCard(isTiny = isTiny, borderColor = verdictColor) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = verdictText,
                                    color = verdictColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Fair: ${estimate.fairPriceRange.totalFairRangeUsd}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "Estimated Parts: $${estimate.fairPriceRange.partsMinUsd}-$${estimate.fairPriceRange.partsMaxUsd} • Labor: ${estimate.fairPriceRange.laborHoursEst} hrs",
                                color = AutoPulseColors.TextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Plain English & Analogy Card
                item {
                    AdaptiveCard(isTiny = isTiny) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = estimate.simpleEnglishTitle,
                                    color = AutoPulseColors.PrimaryCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Urgency: ${estimate.urgencyLabel}",
                                    color = if (estimate.urgencyScore >= 4) AutoPulseColors.CriticalCrimson else AutoPulseColors.WarningAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "What It Does: ${estimate.whatItDoes}",
                                color = AutoPulseColors.TextPrimary,
                                fontSize = 12.sp
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AutoPulseColors.SuccessEmerald.copy(alpha = 0.10f))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "💡 Everyday Analogy: \"${estimate.analogy}\"",
                                    color = AutoPulseColors.SuccessEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Questions to Ask Mechanic Checklist
                item {
                    AdaptiveCard(isTiny = isTiny) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "📋 Questions to Ask Your Mechanic:",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            estimate.questionsToAskMechanic.forEachIndexed { idx, q ->
                                val isChecked = checkedQuestions.contains(idx)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isChecked) checkedQuestions.remove(idx)
                                            else checkedQuestions.add(idx)
                                        }
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            if (it) checkedQuestions.add(idx)
                                            else checkedQuestions.remove(idx)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = AutoPulseColors.PrimaryCyan,
                                            uncheckedColor = AutoPulseColors.SurfaceBorder
                                        )
                                    )
                                    Text(
                                        text = q,
                                        color = if (isChecked) AutoPulseColors.TextTertiary else AutoPulseColors.TextPrimary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Visual Parts Encyclopedia Toggle
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AutoPulseColors.SurfaceDark)
                        .clickable { showEncyclopedia = !showEncyclopedia }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📖 102-Part Visual Encyclopedia",
                            color = AutoPulseColors.PrimaryCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (showEncyclopedia) "Close ▲" else "Explore ▼",
                            color = AutoPulseColors.TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            if (showEncyclopedia) {
                item {
                    OutlinedTextField(
                        value = partSearchQuery,
                        onValueChange = {
                            partSearchQuery = it
                            onIntent(AutoPulseIntent.SearchEncyclopedia(it))
                        },
                        label = { Text("Search 102 Automotive Parts") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(state.encyclopediaEntries.take(15), key = { it.id }) { part ->
                    AdaptiveCard(isTiny = isTiny) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(part.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(part.category.name, color = AutoPulseColors.PrimaryCyan, fontSize = 10.sp)
                            }
                            Text(part.functionSummary, color = AutoPulseColors.TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
