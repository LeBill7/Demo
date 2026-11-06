package com.autopulse.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AutoPulseColors {
    val BackgroundDark = Color(0xFF070B14)
    val SurfaceDark = Color(0xFF0E1626)
    val SurfaceBorder = Color(0xFF1E293B)
    val SurfaceHover = Color(0xFF1A2438)

    val PrimaryCyan = Color(0xFF00F2FE)
    val SuccessEmerald = Color(0xFF10B981)
    val WarningAmber = Color(0xFFF59E0B)
    val CriticalCrimson = Color(0xFFF43F5E)
    val AccentViolet = Color(0xFF8B5CF6)

    val TextPrimary = Color(0xFFF8FAFC)
    val TextSecondary = Color(0xFF94A3B8)
    val TextTertiary = Color(0xFF64748B)
}

val AutoPulseColorScheme = darkColorScheme(
    primary = AutoPulseColors.PrimaryCyan,
    secondary = AutoPulseColors.SuccessEmerald,
    tertiary = AutoPulseColors.WarningAmber,
    error = AutoPulseColors.CriticalCrimson,
    background = AutoPulseColors.BackgroundDark,
    surface = AutoPulseColors.SurfaceDark
)

@Composable
fun AutoPulseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AutoPulseColorScheme,
        content = content
    )
}
