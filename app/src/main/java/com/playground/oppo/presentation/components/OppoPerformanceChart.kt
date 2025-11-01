package com.playground.oppo.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.playground.oppo.domain.model.ChartData
import com.playground.oppo.presentation.uistate.OppoDetailedPerformanceUiState

@Composable
fun OppoPerformanceChart(
    chartData: ChartData,
    onPointSelected: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val points = chartData.points
    val chartHeight = 250.dp

    // Normalize Y values to 0-1 range
    val maxY = points.maxOfOrNull { it.yValue.coerceAtLeast(it.yValuePaidAds) } ?: 100f
    val normalizedPoints = points.map { point ->
        point.copy(
            yValue = point.yValue / maxY,
            yValuePaidAds = point.yValuePaidAds / maxY
        )
    }
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(chartHeight)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        // Placeholder for PointerInput to detect touch and call onPointSelected for tooltip
                        // For production, this would handle drag/tap for interactive indicator
                    }) {
                val padding = 20.dp.toPx()
                val availableWidth = size.width - 2 * padding
                val availableHeight = size.height - 2 * padding
                val stepX = availableWidth / (points.size - 1)
                val pathOrganic = Path()
                val pathPaidAds = Path()

                // 1. Draw Grid Lines (Vertical)
                for (i in 0 until points.size) {
                    val x = padding + i * stepX
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.3f),
                        start = Offset(x, padding),
                        end = Offset(x, size.height - padding),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                // 2. Draw Grid Lines (Horizontal) - Simplified to 4 lines
                for (i in 0..3) {
                    val y = padding + i * (availableHeight / 3)
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.3f),
                        start = Offset(padding, y),
                        end = Offset(size.width - padding, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Path calculation
                normalizedPoints.forEachIndexed { index, point ->
                    val x = padding + index * stepX
                    val yOrganic = size.height - padding - (point.yValue * availableHeight)
                    val yPaidAds = size.height - padding - (point.yValuePaidAds * availableHeight)

                    if (index == 0) {
                        pathOrganic.moveTo(x, yOrganic)
                        pathPaidAds.moveTo(x, yPaidAds)
                    } else {
                        pathOrganic.lineTo(x, yOrganic)
                        pathPaidAds.lineTo(x, yPaidAds)
                    }
                }

                // 3. Draw Area Fill (Organic Sales)
                val areaPath = Path() // 1. Create a new, empty Path
                areaPath.addPath(pathOrganic) // 2. Copy the contents of the line path

                areaPath.lineTo(
                    padding + (points.size - 1) * stepX,
                    size.height - padding
                ) // Bottom right
                areaPath.lineTo(padding, size.height - padding) // Bottom left
                areaPath.close()

                drawPath(
                    path = areaPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            OppoDetailedPerformanceUiState.ChartAreaGradientEnd,
                            OppoDetailedPerformanceUiState.PaidAdsArea // Yellow-Green-like color
                        ),
                        startY = padding,
                        endY = size.height - padding
                    ),
                    alpha = 0.7f
                )

                // 4. Draw Lines and Points
                drawPath(
                    pathOrganic,
                    OppoDetailedPerformanceUiState.OrangeSales,
                    style = Stroke(width = 3.dp.toPx())
                )
                drawPath(
                    pathPaidAds,
                    OppoDetailedPerformanceUiState.PaidAds,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Draw data points (circles)
                normalizedPoints.forEachIndexed { index, point ->
                    val x = padding + index * stepX
                    val yOrganic = size.height - padding - (point.yValue * availableHeight)
                    val yPaidAds = size.height - padding - (point.yValuePaidAds * availableHeight)

                    drawCircle(
                        OppoDetailedPerformanceUiState.OrangeSales,
                        radius = 5.dp.toPx(),
                        center = Offset(x, yOrganic)
                    )
                    drawCircle(
                        OppoDetailedPerformanceUiState.PaidAds,
                        radius = 5.dp.toPx(),
                        center = Offset(x, yPaidAds)
                    )
                }
            }
            // X-Axis Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                chartData.points.forEach { point ->
                    Text(point.xLabel, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
