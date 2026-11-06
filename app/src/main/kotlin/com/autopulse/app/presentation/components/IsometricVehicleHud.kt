package com.autopulse.app.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autopulse.app.domain.model.ComponentHealthStatus
import com.autopulse.app.domain.model.HealthState
import com.autopulse.app.presentation.theme.AutoPulseColors
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun IsometricVehicleHud(
    statuses: List<ComponentHealthStatus>,
    selectedZone: String = "ALL",
    isTiny: Boolean = false,
    modifier: Modifier = Modifier
) {
    var yawAngle by remember { mutableFloatStateOf(0.65f) }
    var pitchAngle by remember { mutableFloatStateOf(0.35f) }

    val infiniteTransition = rememberInfiniteTransition(label = "OverdueRadarPulse")
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarPulseRadius"
    )

    val canvasHeight = if (isTiny) 190.dp else 260.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(canvasHeight)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A0F1D))
            .border(1.dp, AutoPulseColors.SurfaceBorder, RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    yawAngle += dragAmount.x * 0.008f
                    pitchAngle = (pitchAngle + dragAmount.y * 0.006f).coerceIn(0.12f, 1.15f)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(canvasHeight)) {
            val cx = size.width / 2f
            val cy = size.height * 0.55f
            val scale = min(size.width, size.height) * (if (selectedZone == "ALL") 0.44f else 0.52f)

            fun project(x: Float, y: Float, z: Float): Offset {
                val cosY = cos(yawAngle)
                val sinY = sin(yawAngle)
                val cosP = cos(pitchAngle)
                val sinP = sin(pitchAngle)

                val rx = x * cosY - z * sinY
                val rz = x * sinY + z * cosY
                val ry = y * cosP - rz * sinP
                return Offset(cx + rx * scale, cy - ry * scale)
            }

            // Chassis rails
            val chassisEdges = listOf(
                Triple(-0.46f, 0.16f, -0.30f) to Triple(0.46f, 0.16f, -0.30f),
                Triple(-0.46f, 0.16f, 0.30f) to Triple(0.46f, 0.16f, 0.30f),
                Triple(-0.46f, 0.38f, -0.28f) to Triple(0.46f, 0.38f, -0.28f),
                Triple(-0.46f, 0.38f, 0.28f) to Triple(0.46f, 0.38f, 0.28f),
                Triple(-0.18f, 0.58f, -0.22f) to Triple(0.14f, 0.58f, -0.22f),
                Triple(-0.18f, 0.58f, 0.22f) to Triple(0.14f, 0.58f, 0.22f)
            )
            for ((start, end) in chassisEdges) {
                drawLine(
                    color = AutoPulseColors.PrimaryCyan.copy(alpha = 0.5f),
                    start = project(start.first, start.second, start.third),
                    end = project(end.first, end.second, end.third),
                    strokeWidth = if (isTiny) 2f else 3f
                )
            }

            // Component Hotspots
            val filtered = statuses.filter {
                selectedZone == "ALL" || it.hotspot.zone.equals(selectedZone, ignoreCase = true)
            }
            for (item in filtered) {
                val pt = project(item.hotspot.x, item.hotspot.y, item.hotspot.z)
                val nodeColor = when (item.state) {
                    HealthState.OVERDUE -> AutoPulseColors.CriticalCrimson
                    HealthState.DUE_SOON -> AutoPulseColors.WarningAmber
                    HealthState.HEALTHY -> AutoPulseColors.SuccessEmerald
                }

                if (item.state == HealthState.OVERDUE) {
                    val rippleRadius = (if (isTiny) 10f else 14f) + radarPulse * (if (isTiny) 18f else 26f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AutoPulseColors.CriticalCrimson.copy(alpha = 0.75f * (1f - radarPulse)),
                                Color.Transparent
                            ),
                            center = pt,
                            radius = rippleRadius
                        ),
                        radius = rippleRadius,
                        center = pt
                    )
                }

                drawCircle(
                    color = nodeColor,
                    radius = if (item.state == HealthState.OVERDUE) (if (isTiny) 6f else 8f) else (if (isTiny) 4f else 5.5f),
                    center = pt
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .background(Color(0x99070B14), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Drag to Rotate 360°",
                color = AutoPulseColors.TextTertiary,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun AnimatedHealthRing(
    remainingLifeFraction: Float,
    state: HealthState,
    ringSize: Dp = 64.dp,
    strokeWidth: Dp = 7.dp,
    modifier: Modifier = Modifier
) {
    val animatedSweep by animateFloatAsState(
        targetValue = (remainingLifeFraction.coerceIn(0f, 1f)) * 360f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "HealthRingSweep"
    )
    val ringColor = when (state) {
        HealthState.HEALTHY -> AutoPulseColors.PrimaryCyan
        HealthState.DUE_SOON -> AutoPulseColors.WarningAmber
        HealthState.OVERDUE -> AutoPulseColors.CriticalCrimson
    }

    Canvas(modifier = modifier.size(ringSize)) {
        val stroke = strokeWidth.toPx()
        drawArc(
            color = Color(0xFF1E293B),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
        drawArc(
            color = ringColor,
            startAngle = -90f,
            sweepAngle = animatedSweep,
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
    }
}
