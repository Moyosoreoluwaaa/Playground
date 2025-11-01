package com.playground.layer.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable

@Composable
fun PriceChart(
    data: List<Float>,
    currentPrice: Float,
    isPositiveChange: Boolean,
    onIndicatorMove: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    // Simplified Canvas for Preview
    Canvas(
        modifier = modifier
            .height(180.dp)
            .fillMaxWidth()
    ) {
        val path = Path()
        val stepX = size.width / (data.size - 1)

        data.forEachIndexed { index, price ->
            val x = index * stepX
            val y = size.height - (price / 100f * size.height) // Normalize and invert for chart
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = if (isPositiveChange) Color.Yellow else Color.Red,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}