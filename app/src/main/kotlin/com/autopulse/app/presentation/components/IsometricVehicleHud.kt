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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.autopulse.app.domain.model.ComponentHealthStatus
import com.autopulse.app.domain.model.HealthState
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun IsometricVehicleHud(
    statuses: List<ComponentHealthStatus>,
    selectedZone: String = "ALL",
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0A0F1D))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    yawAngle += dragAmount.x * 0.008f
                    pitchAngle = (pitchAngle + dragAmount.y * 0.006f).coerceIn(0.12f, 1.15f)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(260.dp)) {
            val cx = size.width / 2f
            val cy = size.height * 0.56f
            val scale = min(size.width, size.height) * (if (selectedZone == "ALL") 0.42f else 0.52f)

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

            // Isometric wireframe chassis rails
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
                    color = Color(0xFF00F2FE).copy(alpha = 0.5f),
                    start = project(start.first, start.second, start.third),
                    end = project(end.first, end.second, end.third),
                    strokeWidth = 3f
                )
            }

            // Render component hotspots with pulsing radial shader ripple for OVERDUE items
            val filtered = statuses.filter {
                selectedZone == "ALL" || it.hotspot.zone.equals(selectedZone, ignoreCase = true)
            }
            for (item in filtered) {
                val pt = project(item.hotspot.x, item.hotspot.y, item.hotspot.z)
                val nodeColor = when (item.state) {
                    HealthState.OVERDUE -> Color(0xFFF43F5E)
                    HealthState.DUE_SOON -> Color(0xFFF59E0B)
                    HealthState.HEALTHY -> Color(0xFF10B981)
                }

                if (item.state == HealthState.OVERDUE) {
                    val rippleRadius = 14f + radarPulse * 26f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFF43F5E).copy(alpha = 0.75f * (1f - radarPulse)),
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
                    radius = if (item.state == HealthState.OVERDUE) 8f else 5.5f,
                    center = pt
                )
            }
        }
    }
}

@Composable
fun AnimatedHealthRing(
    remainingLifeFraction: Float,
    state: HealthState,
    modifier: Modifier = Modifier
) {
    val animatedSweep by animateFloatAsState(
        targetValue = (remainingLifeFraction.coerceIn(0f, 1f)) * 360f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "HealthRingSweep"
    )
    val ringColor = when (state) {
        HealthState.HEALTHY -> Color(0xFF00F2FE)
        HealthState.DUE_SOON -> Color(0xFFF59E0B)
        HealthState.OVERDUE -> Color(0xFFF43F5E)
    }

    Canvas(modifier = modifier.size(64.dp)) {
        val stroke = 7.dp.toPx()
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
