package com.playground.lamme

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

@Composable
fun BrightnessBar(
    modifier: Modifier = Modifier,
    barColor: Color = Color.White,
    accentColor: Color = Color.Red,
    onBrightnessChange: (Float) -> Unit = {}
) {
    val context = LocalContext.current
    var brightness by remember { mutableFloatStateOf(0.5f) }
    
    // Apply brightness to screen
    LaunchedEffect(brightness) {
        val activity = context as? Activity
        activity?.window?.let { window ->
            val params = window.attributes
            params.screenBrightness = brightness
            window.attributes = params
        }
        onBrightnessChange(brightness)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(60.dp)
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val newBrightness = (tapOffset.x / size.width).coerceIn(0f, 1f)
                        brightness = newBrightness
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val newBrightness = (change.position.x / size.width).coerceIn(0f, 1f)
                        brightness = newBrightness
                    }
                }
        ) {
            val barHeight = 12f
            val barY = size.height / 2 - barHeight / 2
            val barWidth = size.width

            // Background bar
            drawRect(
                color = barColor.copy(alpha = 0.2f),
                topLeft = Offset(0f, barY),
                size = Size(barWidth, barHeight)
            )

            // Filled portion
            drawRect(
                color = accentColor,
                topLeft = Offset(0f, barY),
                size = Size(barWidth * brightness, barHeight)
            )

            // Outline
            drawRect(
                color = barColor,
                topLeft = Offset(0f, barY),
                size = Size(barWidth, barHeight),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )

            // Slider handle
            val handleX = barWidth * brightness
            val handleRadius = 16f
            
            // Handle glow
            drawCircle(
                color = accentColor.copy(alpha = 0.3f),
                radius = handleRadius + 8f,
                center = Offset(handleX, size.height / 2)
            )

            // Handle
            drawCircle(
                color = accentColor,
                radius = handleRadius,
                center = Offset(handleX, size.height / 2)
            )

            // Handle center
            drawCircle(
                color = Color.Black,
                radius = 6f,
                center = Offset(handleX, size.height / 2)
            )

            // Tick marks
            val tickCount = 10
            for (i in 0..tickCount) {
                val tickX = (barWidth / tickCount) * i
                val tickHeight = if (i % 5 == 0) 8f else 4f
                
                drawLine(
                    color = barColor.copy(alpha = 0.5f),
                    start = Offset(tickX, barY - tickHeight),
                    end = Offset(tickX, barY),
                    strokeWidth = 1f
                )
            }

            // Brightness percentage dots
            val dotY = barY + barHeight + 20f
            val percentage = (brightness * 100).toInt()
            val percentText = percentage.toString().padStart(3, '0')
            
            var dotX = (size.width - (percentText.length * 30f)) / 2
            percentText.forEach { char ->
                drawDottedDigit(char, Offset(dotX, dotY), barColor)
                dotX += 30f
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDottedDigit(
    char: Char,
    position: Offset,
    color: Color
) {
    val dotSize = 2f
    val spacing = 4f
    
    val pattern = when (char) {
        '0' -> listOf(
            listOf(1, 1, 1),
            listOf(1, 0, 1),
            listOf(1, 0, 1),
            listOf(1, 0, 1),
            listOf(1, 1, 1)
        )
        '1' -> listOf(
            listOf(0, 1, 0),
            listOf(1, 1, 0),
            listOf(0, 1, 0),
            listOf(0, 1, 0),
            listOf(1, 1, 1)
        )
        '2' -> listOf(
            listOf(1, 1, 1),
            listOf(0, 0, 1),
            listOf(1, 1, 1),
            listOf(1, 0, 0),
            listOf(1, 1, 1)
        )
        '3' -> listOf(
            listOf(1, 1, 1),
            listOf(0, 0, 1),
            listOf(1, 1, 1),
            listOf(0, 0, 1),
            listOf(1, 1, 1)
        )
        '4' -> listOf(
            listOf(1, 0, 1),
            listOf(1, 0, 1),
            listOf(1, 1, 1),
            listOf(0, 0, 1),
            listOf(0, 0, 1)
        )
        '5' -> listOf(
            listOf(1, 1, 1),
            listOf(1, 0, 0),
            listOf(1, 1, 1),
            listOf(0, 0, 1),
            listOf(1, 1, 1)
        )
        '6' -> listOf(
            listOf(1, 1, 1),
            listOf(1, 0, 0),
            listOf(1, 1, 1),
            listOf(1, 0, 1),
            listOf(1, 1, 1)
        )
        '7' -> listOf(
            listOf(1, 1, 1),
            listOf(0, 0, 1),
            listOf(0, 1, 0),
            listOf(0, 1, 0),
            listOf(0, 1, 0)
        )
        '8' -> listOf(
            listOf(1, 1, 1),
            listOf(1, 0, 1),
            listOf(1, 1, 1),
            listOf(1, 0, 1),
            listOf(1, 1, 1)
        )
        '9' -> listOf(
            listOf(1, 1, 1),
            listOf(1, 0, 1),
            listOf(1, 1, 1),
            listOf(0, 0, 1),
            listOf(1, 1, 1)
        )
        else -> List(5) { List(3) { 0 } }
    }

    pattern.forEachIndexed { row, cols ->
        cols.forEachIndexed { col, value ->
            if (value == 1) {
                drawCircle(
                    color = color,
                    radius = dotSize,
                    center = Offset(
                        position.x + col * spacing,
                        position.y + row * spacing
                    )
                )
            }
        }
    }
}