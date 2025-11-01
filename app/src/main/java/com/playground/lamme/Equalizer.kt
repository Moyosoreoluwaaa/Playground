package com.playground.lamme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

data class EqualizerBar(
    val index: Int,
    var targetHeight: Float,
    var currentHeight: Float,
    var velocity: Float = 0f
)

@Composable
fun EqualizerEffect(
    modifier: Modifier = Modifier,
    barColor: Color = Color.White,
    activeColor: Color = Color.Red,
    barCount: Int = 32
) {
    var bars by remember {
        mutableStateOf(
            List(barCount) { index ->
                EqualizerBar(
                    index = index,
                    targetHeight = 0.2f,
                    currentHeight = 0.2f
                )
            }
        )
    }

    var isActive by remember { mutableStateOf(false) }
    var touchPosition by remember { mutableStateOf<Offset?>(null) }
    var animationTime by remember { mutableFloatStateOf(0f) }

    // Animation loop
    LaunchedEffect(isActive) {
        while (true) {
            delay(16) // ~60 FPS
            animationTime += 0.05f

            bars = bars.map { bar ->
                var newTarget = bar.targetHeight

                // Auto-animate if active
                if (isActive) {
                    val wave = sin(animationTime + bar.index * 0.3).toFloat()
                    newTarget = 0.3f + wave * 0.4f
                }

                // Smooth animation with spring physics
                val diff = newTarget - bar.currentHeight
                val newVelocity = (bar.velocity + diff * 0.15f) * 0.85f
                val newHeight = (bar.currentHeight + newVelocity).coerceIn(0.1f, 1f)

                bar.copy(
                    targetHeight = newTarget,
                    currentHeight = newHeight,
                    velocity = newVelocity
                )
            }

            // Decay to idle if not touched recently
            if (!isActive && touchPosition == null) {
                bars = bars.map { bar ->
                    bar.copy(targetHeight = bar.targetHeight * 0.98f + 0.2f * 0.02f)
                }
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isActive = true
                        touchPosition = it
                        tryAwaitRelease()
                        isActive = false
                        touchPosition = null
                    },
                    onTap = { tapOffset ->
                        val barWidth = size.width / barCount
                        val tappedBarIndex = (tapOffset.x / barWidth).toInt()
                            .coerceIn(0, barCount - 1)

                        bars = bars.mapIndexed { index, bar ->
                            val distance = abs(index - tappedBarIndex)
                            val impact = (1f - distance / barCount.toFloat()).coerceIn(0f, 1f)
                            bar.copy(targetHeight = 0.3f + impact * 0.7f)
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isActive = true
                        touchPosition = it
                    },
                    onDragEnd = {
                        isActive = false
                        touchPosition = null
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        touchPosition = change.position

                        val barWidth = size.width / barCount
                        val draggedBarIndex = (change.position.x / barWidth).toInt()
                            .coerceIn(0, barCount - 1)

                        bars = bars.mapIndexed { index, bar ->
                            val distance = abs(index - draggedBarIndex)
                            val impact = (1f - distance / 5f).coerceIn(0f, 1f)

                            if (impact > 0) {
                                bar.copy(targetHeight = 0.4f + impact * 0.6f)
                            } else {
                                bar
                            }
                        }
                    }
                )
            }
    ) {
        val barWidth = size.width / barCount
        val maxBarHeight = size.height * 0.8f
        val baseY = size.height / 2

        bars.forEach { bar ->
            val x = bar.index * barWidth
            val barHeight = maxBarHeight * bar.currentHeight

            // Determine color based on height
            val colorProgress = bar.currentHeight
            val currentColor = if (colorProgress > 0.6f) {
                val t = (colorProgress - 0.6f) / 0.4f
                Color(
                    red = barColor.red * (1 - t) + activeColor.red * t,
                    green = barColor.green * (1 - t) + activeColor.green * t,
                    blue = barColor.blue * (1 - t) + activeColor.blue * t,
                    alpha = barColor.alpha
                )
            } else {
                barColor
            }

            // Draw bar extending up from center
            drawRect(
                color = currentColor,
                topLeft = Offset(x + 2f, baseY - barHeight / 2),
                size = Size(barWidth - 4f, barHeight)
            )

            // Draw reflection/mirror below
            drawRect(
                color = currentColor.copy(alpha = 0.3f),
                topLeft = Offset(x + 2f, baseY + barHeight / 2),
                size = Size(barWidth - 4f, -barHeight)
            )

            // Peak dot
            if (bar.currentHeight > 0.7f) {
                drawCircle(
                    color = activeColor,
                    radius = 3f,
                    center = Offset(x + barWidth / 2, baseY - barHeight / 2 - 5f)
                )
            }
        }

        // Center line
        drawLine(
            color = barColor.copy(alpha = 0.5f),
            start = Offset(0f, baseY),
            end = Offset(size.width, baseY),
            strokeWidth = 1f
        )

        // Touch indicator
        touchPosition?.let { pos ->
            drawCircle(
                color = activeColor.copy(alpha = 0.5f),
                radius = 20f,
                center = pos
            )
            drawCircle(
                color = activeColor,
                radius = 10f,
                center = pos
            )
        }
    }
}