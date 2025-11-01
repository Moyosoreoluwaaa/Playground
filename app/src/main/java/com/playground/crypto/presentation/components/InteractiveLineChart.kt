package com.playground.crypto.presentation.components

import android.graphics.PointF
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlin.random.Random
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap

@Composable
fun InteractiveLineChart(
    data: List<PointF>,
    modifier: Modifier = Modifier,
) {
    var touchX by remember { mutableFloatStateOf(-1f) }
    val path = remember(data) { Path() }
    val lineAndIndicatorColor = MaterialTheme.colorScheme.primary
    val lineAndIndicatorSecondColor = MaterialTheme.colorScheme.primary

    // Memoize the paint object for performance
    val textPaint = remember {
        Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = 40f
            color = android.graphics.Color.WHITE // Using Android color for the native canvas
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        touchX = offset.x.coerceIn(0f, size.width.toFloat())
                    },
                    onDragEnd = { touchX = -1f },
                    onDragCancel = { touchX = -1f },
                    onDrag = { change, _ ->
                        touchX = change.position.x.coerceIn(0f, size.width.toFloat())
                    }
                )
            }
    ) {
        if (data.size < 2) return@Canvas // Need at least 2 points to draw a line

        val minX = data.minOf { it.x }
        val maxX = data.maxOf { it.x }
        val minY = data.minOf { it.y }
        val maxY = data.maxOf { it.y }
        val xRange = (maxX - minX).takeIf { it > 0 } ?: 1f
        val yRange = (maxY - minY).takeIf { it > 0 } ?: 1f

        // Function to transform data points to canvas coordinates
        fun PointF.toCanvasPoint(): PointF {
            val canvasX = (this.x - minX) / xRange * size.width
            val canvasY = size.height - ((this.y - minY) / yRange * size.height)
            return PointF(canvasX, canvasY)
        }

        // Build the path
        path.reset()
        data.forEachIndexed { index, point ->
            val canvasPoint = point.toCanvasPoint()
            if (index == 0) {
                path.moveTo(canvasPoint.x, canvasPoint.y)
            } else {
                path.lineTo(canvasPoint.x, canvasPoint.y)
            }
        }

        // Draw the line graph
        drawPath(
            path,
            color = lineAndIndicatorColor,
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )

        // If user is interacting with the chart
        if (touchX >= 0f) {
            // Find the closest data point to the touch position
            val dataIndex =
                ((touchX / size.width) * (data.size - 1)).roundToInt().coerceIn(0, data.size - 1)
            val closestDataPoint = data[dataIndex]
            val canvasPoint = closestDataPoint.toCanvasPoint()

            // Draw vertical indicator line
            drawLine(
                color = lineAndIndicatorColor.copy(alpha = 0.5f),
                start = Offset(canvasPoint.x, 0f),
                end = Offset(canvasPoint.x, size.height),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )

            // Draw circle on the path
            drawCircle(
                color = lineAndIndicatorColor,
                radius = 12f,
                center = Offset(canvasPoint.x, canvasPoint.y)
            )
            drawCircle(
                color = lineAndIndicatorSecondColor,
                radius = 8f,
                center = Offset(canvasPoint.x, canvasPoint.y)
            )

            // Draw tooltip
            val tooltipText = "$%.2f".format(closestDataPoint.y)
            val textBounds = Rect()
            textPaint.getTextBounds(tooltipText, 0, tooltipText.length, textBounds)

            val tooltipWidth = textBounds.width() + 40f
            val tooltipHeight = textBounds.height() + 30f
            val tooltipCornerRadius = CornerRadius(16f, 16f)
            val tooltipTop = 0f
            val tooltipLeft =
                (canvasPoint.x - tooltipWidth / 2).coerceIn(0f, size.width - tooltipWidth)

            // **FIX 1: Using correct drawRoundRect overload**
            drawRoundRect(
                color = lineAndIndicatorColor,
                topLeft = Offset(tooltipLeft, tooltipTop),
                size = Size(tooltipWidth, tooltipHeight),
                cornerRadius = tooltipCornerRadius
            )

            // **FIX 2: Using drawContext.canvas.nativeCanvas**
            drawContext.canvas.nativeCanvas.drawText(
                tooltipText,
                tooltipLeft + tooltipWidth / 2,
                tooltipTop + tooltipHeight / 2 + textBounds.height() / 2,
                textPaint
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun InteractiveLineChartPreview() {
    val sampleData = remember {
        (0..30).map {
            val x = it.toFloat()
            val y = Random.nextDouble(1200.0, 1250.0).toFloat()
            PointF(x, y)
        }
    }
    Box(modifier = Modifier.padding(16.dp)) {
        InteractiveLineChart(data = sampleData)
    }
}
