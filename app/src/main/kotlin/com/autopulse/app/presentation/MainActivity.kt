package com.autopulse.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autopulse.app.core.version.AppVersion
import com.autopulse.app.presentation.screens.DriverGarageScreen
import com.autopulse.app.presentation.screens.MechanicTranslatorScreen
import com.autopulse.app.presentation.screens.PartnerShopTerminalScreen
import com.autopulse.app.presentation.screens.TrustPassportScreen
import com.autopulse.app.presentation.theme.AutoPulseColors
import com.autopulse.app.presentation.theme.AutoPulseTheme
import com.autopulse.app.presentation.viewmodel.AutoPulseViewModel

enum class AutoPulseTab(val shortTitle: String, val fullTitle: String) {
    GARAGE("Garage", "1. Garage"),
    TRANSLATOR("Copilot", "2. Copilot"),
    SHOP("Bay", "3. Shop Bay"),
    PASSPORT("Trust", "4. Passport")
}

class MainActivity : ComponentActivity() {
    private val viewModel = AutoPulseViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoPulseTheme {
                AutoPulseRootScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AutoPulseRootScreen(viewModel: AutoPulseViewModel) {
    val state by viewModel.uiState.collectAsState()
    var activeTab by remember { mutableStateOf(AutoPulseTab.GARAGE) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(AutoPulseColors.BackgroundDark)) {
        val isTiny = maxWidth < 300.dp
        val horizontalPad = if (isTiny) 8.dp else 16.dp

        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AutoPulseColors.SurfaceDark)
                        .border(width = 0.5.dp, color = AutoPulseColors.SurfaceBorder)
                        .padding(horizontal = horizontalPad, vertical = if (isTiny) 8.dp else 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "AUTOPULSE",
                            color = AutoPulseColors.PrimaryCyan,
                            fontSize = if (isTiny) 14.sp else 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        state.activeVehicle?.let { v ->
                            Text(
                                text = "• ${v.model}",
                                color = AutoPulseColors.TextSecondary,
                                fontSize = if (isTiny) 10.sp else 12.sp
                            )
                        }
                    }
                    Text(
                        text = "v${AppVersion.VERSION_NAME}",
                        color = AutoPulseColors.TextTertiary,
                        fontSize = if (isTiny) 9.sp else 11.sp
                    )
                }
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AutoPulseColors.SurfaceDark)
                        .border(width = 0.5.dp, color = AutoPulseColors.SurfaceBorder)
                        .padding(horizontal = if (isTiny) 4.dp else 8.dp, vertical = if (isTiny) 4.dp else 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(if (isTiny) 3.dp else 6.dp)
                ) {
                    AutoPulseTab.entries.forEach { tab ->
                        val selected = activeTab == tab
                        val label = if (isTiny) tab.shortTitle else tab.fullTitle

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(if (isTiny) 38.dp else 44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selected) AutoPulseColors.PrimaryCyan else AutoPulseColors.BackgroundDark
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (selected) AutoPulseColors.PrimaryCyan else AutoPulseColors.SurfaceBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { activeTab = tab },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = if (isTiny) 10.sp else 11.sp,
                                fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                                color = if (selected) Color(0xFF070B14) else Color.White
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (activeTab) {
                    AutoPulseTab.GARAGE -> DriverGarageScreen(
                        state = state,
                        onIntent = viewModel::dispatch
                    )
                    AutoPulseTab.TRANSLATOR -> MechanicTranslatorScreen(
                        state = state,
                        onIntent = viewModel::dispatch
                    )
                    AutoPulseTab.SHOP -> PartnerShopTerminalScreen(
                        state = state,
                        onIntent = viewModel::dispatch
                    )
                    AutoPulseTab.PASSPORT -> TrustPassportScreen(
                        state = state,
                        onIntent = viewModel::dispatch
                    )
                }
            }
        }
    }
}
