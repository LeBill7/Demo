package com.autopulse.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autopulse.app.domain.model.HealthState
import com.autopulse.app.presentation.theme.AutoPulseColors

enum class WindowWidthClass {
    TINY,
    COMPACT,
    EXPANDED;

    val isTiny: Boolean get() = this == TINY
    val screenPadding: Dp get() = if (this == TINY) 8.dp else 16.dp
}

@Composable
fun AdaptiveContainer(
    modifier: Modifier = Modifier,
    content: @Composable (widthClass: WindowWidthClass) -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(AutoPulseColors.BackgroundDark)) {
        val widthClass = when {
            maxWidth < 300.dp -> WindowWidthClass.TINY
            maxWidth < 600.dp -> WindowWidthClass.COMPACT
            else -> WindowWidthClass.EXPANDED
        }
        content(widthClass)
    }
}

@Composable
fun AdaptiveCard(
    modifier: Modifier = Modifier,
    isTiny: Boolean = false,
    borderColor: Color = AutoPulseColors.SurfaceBorder,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cornerRadius = if (isTiny) 8.dp else 12.dp
    val cardPadding = if (isTiny) 8.dp else 14.dp
    val baseModifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(cornerRadius))
        .background(AutoPulseColors.SurfaceDark)
        .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))

    val finalModifier = if (onClick != null) {
        baseModifier.clickable { onClick() }
    } else {
        baseModifier
    }.padding(cardPadding)

    Box(modifier = finalModifier) {
        content()
    }
}

@Composable
fun StatusPill(
    label: String,
    state: HealthState,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (state) {
        HealthState.HEALTHY -> Color(0xFF10B981).copy(alpha = 0.15f) to AutoPulseColors.SuccessEmerald
        HealthState.DUE_SOON -> Color(0xFFF59E0B).copy(alpha = 0.15f) to AutoPulseColors.WarningAmber
        HealthState.OVERDUE -> Color(0xFFF43F5E).copy(alpha = 0.20f) to AutoPulseColors.CriticalCrimson
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
