package com.playground.lamme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.sqrt

data class Ripple(
    val origin: Offset,
    val startTime: Long,
    val maxRadius: Float = 300f,
    val duration: Long = 2000L
)

@Composable
fun WaterRippleEffect(
    modifier: Modifier = Modifier,
    waterColor: Color = Color.Cyan.copy(alpha = 0.3f),
    rippleColor: Color = Color.White
) {
    var ripples by remember { mutableStateOf<List<Ripple>>(emptyList()) }
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Animation loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // ~60 FPS
            currentTime = System.currentTimeMillis()

            // Remove expired ripples
            ripples = ripples.filter { ripple ->
                currentTime - ripple.startTime < ripple.duration
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    ripples = ripples + Ripple(
                        origin = tapOffset,
                        startTime = System.currentTimeMillis()
                    )
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    ripples = ripples + Ripple(
                        origin = change.position,
                        startTime = System.currentTimeMillis(),
                        maxRadius = 150f,
                        duration = 1000L
                    )
                }
            }
    ) {
        // Draw calm water background with grid
        drawWaterGrid(waterColor)

        // Draw all active ripples
        ripples.forEach { ripple ->
            val elapsed = currentTime - ripple.startTime
            val progress = elapsed.toFloat() / ripple.duration.toFloat()

            if (progress < 1f) {
                drawRipple(ripple, progress, rippleColor)
            }
        }
    }
}

private fun DrawScope.drawWaterGrid(color: Color) {
    val gridSpacing = 40f
    val cols = (size.width / gridSpacing).toInt()
    val rows = (size.height / gridSpacing).toInt()

    // Horizontal lines
    for (i in 0..rows) {
        val y = i * gridSpacing
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
    }

    // Vertical lines
    for (i in 0..cols) {
        val x = i * gridSpacing
        drawLine(
            color = color,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawRipple(ripple: Ripple, progress: Float, color: Color) {
    val currentRadius = ripple.maxRadius * progress
    val alpha = 1f - progress // Fade out as ripple expands

    // Multiple concentric circles for wave effect
    for (i in 0..3) {
        val offset = i * 15f
        val radius = currentRadius - offset

        if (radius > 0) {
            val waveAlpha = (alpha * (1f - i * 0.2f)).coerceIn(0f, 1f)

            drawCircle(
                color = color.copy(alpha = waveAlpha * 0.6f),
                radius = radius,
                center = ripple.origin,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 2f + (3 - i) * 1f
                )
            )
        }
    }

    // Distortion effect on grid (simulated with dots)
    val distortionRadius = currentRadius * 1.5f
    val dotCount = 12

    for (i in 0 until dotCount) {
        val angle = (i * 360f / dotCount) * Math.PI / 180
        val distance = distortionRadius * (0.8f + 0.2f * kotlin.math.sin(progress * Math.PI).toFloat())

        val dotX = ripple.origin.x + distance * kotlin.math.cos(angle).toFloat()
        val dotY = ripple.origin.y + distance * kotlin.math.sin(angle).toFloat()

        if (dotX in 0f..size.width && dotY in 0f..size.height) {
            drawCircle(
                color = color.copy(alpha = alpha * 0.4f),
                radius = 3f * (1f - progress),
                center = Offset(dotX, dotY)
            )
        }
    }
}