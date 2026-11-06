package com.autopulse.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autopulse.app.core.version.AppVersion
import com.autopulse.app.presentation.screens.DriverGarageScreen
import com.autopulse.app.presentation.screens.MechanicTranslatorScreen
import com.autopulse.app.presentation.screens.PartnerShopTerminalScreen
import com.autopulse.app.presentation.screens.TrustPassportScreen
import com.autopulse.app.presentation.viewmodel.AutoPulseViewModel

enum class AutoPulseTab(val title: String) {
    GARAGE("1. Garage HUD"),
    TRANSLATOR("2. Translator"),
    SHOP("3. Shop Bay"),
    PASSPORT("4. Passport")
}

private val AutoPulseColorScheme = darkColorScheme(
    primary = Color(0xFF00F2FE),
    secondary = Color(0xFF10B981),
    tertiary = Color(0xFFF59E0B),
    background = Color(0xFF070B14),
    surface = Color(0xFF0E1626)
)

class MainActivity : ComponentActivity() {
    private val viewModel = AutoPulseViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = AutoPulseColorScheme) {
                AutoPulseRootScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AutoPulseRootScreen(viewModel: AutoPulseViewModel) {
    val state by viewModel.uiState.collectAsState()
    var activeTab by remember { mutableStateOf(AutoPulseTab.GARAGE) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0E1626))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AUTOPULSE",
                    color = Color(0xFF00F2FE),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "v${AppVersion.VERSION_NAME}",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0E1626))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AutoPulseTab.entries.forEach { tab ->
                    val selected = activeTab == tab
                    Button(
                        onClick = { activeTab = tab },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) Color(0xFF00F2FE) else Color(0xFF1E293B),
                            contentColor = if (selected) Color(0xFF070B14) else Color.White
                        )
                    ) {
                        Text(tab.title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.weight(1f)) {
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
