package com.playground.crypto.presentation.bookish// In a new file, e.g., components/AudioProgressChart.kt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun AudioProgressChart(
    chartData: List<Pair<Float, String>>,
    progress: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val indicatorRadius = 8.dp
    val col =  MaterialTheme.colorScheme

    Canvas(modifier = modifier
        .fillMaxWidth()
        .height(80.dp)
        .pointerInput(Unit) {
            detectHorizontalDragGestures { change, _ ->
                val newProgress = (change.position.x / size.width).coerceIn(0f, 1f)
                onProgressChange(newProgress)
                change.consume()
            }
        }
    ) {
        val width = size.width
        val height = size.height
        val progressX = progress * width

        // Draw background line
        drawLine(
            color = col.onSurfaceVariant.copy(alpha = 0.4f),
            start = Offset(0f, height / 2f),
            end = Offset(width, height / 2f),
            strokeWidth = 2.dp.toPx()
        )

        // Draw progress line
        drawLine(
            color = col.primary,
            start = Offset(0f, height / 2f),
            end = Offset(progressX, height / 2f),
            strokeWidth = 2.dp.toPx()
        )

        // Draw the interactive indicator
        drawCircle(
            color = col.primary,
            radius = indicatorRadius.toPx(),
            center = Offset(progressX, height / 2f)
        )
    }
}