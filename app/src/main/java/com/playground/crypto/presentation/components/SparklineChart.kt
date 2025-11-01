package com.playground.crypto.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
private fun SparklineChart(data: List<Float>, modifier: Modifier = Modifier) {
    val graphColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier) {
        val path = Path()
        val maxValue = data.maxOrNull() ?: 0f
        val minValue = data.minOrNull() ?: 0f
        val range = maxValue - minValue

        data.forEachIndexed { index, value ->
            val x = size.width * (index.toFloat() / (data.size - 1))
            val y = size.height * (1 - ((value - minValue) / range))

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        drawPath(
            path = path,
            color = graphColor,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
    }
}
