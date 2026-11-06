package com.autopulse.app.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autopulse.app.presentation.theme.AutoPulseColors

@Composable
fun RollingOdometer(
    mileage: Int,
    modifier: Modifier = Modifier,
    isTiny: Boolean = false,
    digitCount: Int = 6,
    onClick: (() -> Unit)? = null
) {
    val formattedMileage = remember(mileage, digitCount) {
        mileage.coerceAtLeast(0).toString().padStart(digitCount, '0')
    }

    val digitBoxWidth = if (isTiny) 18.dp else 26.dp
    val digitBoxHeight = if (isTiny) 32.dp else 44.dp
    val digitFontSize = if (isTiny) 18.sp else 28.sp
    val commaFontSize = if (isTiny) 18.sp else 28.sp
    val unitFontSize = if (isTiny) 11.sp else 14.sp
    val containerPaddingH = if (isTiny) 8.dp else 12.dp
    val containerPaddingV = if (isTiny) 4.dp else 8.dp

    val baseMod = modifier
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF16191F))
        .border(1.dp, AutoPulseColors.SurfaceBorder, RoundedCornerShape(8.dp))
        .padding(horizontal = containerPaddingH, vertical = containerPaddingV)

    val finalMod = if (onClick != null) {
        baseMod.clickable { onClick() }
    } else {
        baseMod
    }

    Row(
        modifier = finalMod,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        formattedMileage.forEachIndexed { index, char ->
            SingleDigitTumbler(
                digit = char,
                boxWidth = digitBoxWidth,
                boxHeight = digitBoxHeight,
                fontSize = digitFontSize
            )
            if (index == 2 && digitCount == 6) {
                Text(
                    text = ",",
                    color = Color.Gray,
                    fontSize = commaFontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 1.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(if (isTiny) 4.dp else 8.dp))
        Text(
            text = "MI",
            color = AutoPulseColors.PrimaryCyan,
            fontSize = unitFontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun SingleDigitTumbler(
    digit: Char,
    boxWidth: androidx.compose.ui.unit.Dp,
    boxHeight: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    Box(
        modifier = Modifier
            .width(boxWidth)
            .height(boxHeight)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF0B0D11)),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = digit,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically(
                        animationSpec = spring(stiffness = 300f, dampingRatio = 0.75f)
                    ) { it } togetherWith slideOutVertically { -it }
                } else {
                    slideInVertically { -it } togetherWith slideOutVertically { it }
                }
            },
            label = "DigitRollAnimation"
        ) { targetDigit ->
            Text(
                text = targetDigit.toString(),
                color = Color.White,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
