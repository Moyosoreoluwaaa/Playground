package com.playground.bookish.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun AudioWaveform(
    waveformData: List<Float>,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
    progress: Float
) {
    Canvas(modifier = modifier
        .fillMaxWidth()
        .height(100.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = width / waveformData.size
        val halfHeight = height / 2f
        val progressX = progress * width

        val activePath = Path()
        val inactivePath = Path()

        waveformData.forEachIndexed { index, value ->
            val barHeight = value * halfHeight
            val x = index * barWidth + barWidth / 2f

            if (x < progressX) {
                // Draw active part of the waveform
                activePath.moveTo(x, halfHeight - barHeight / 2f)
                activePath.lineTo(x, halfHeight + barHeight / 2f)
            } else {
                // Draw inactive part
                inactivePath.moveTo(x, halfHeight - barHeight / 2f)
                inactivePath.lineTo(x, halfHeight + barHeight / 2f)
            }
        }

        drawPath(
            path = inactivePath,
            color = inactiveColor,
            style = Stroke(width = 2.dp.toPx())
        )
        drawPath(
            path = activePath,
            color = activeColor,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
