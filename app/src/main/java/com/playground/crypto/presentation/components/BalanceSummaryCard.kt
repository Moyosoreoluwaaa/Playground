package com.playground.crypto.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import java.text.NumberFormat

@Composable
fun BalanceSummaryCard(
    totalBalance: String,
    dailyChange: Double,
    chartData: List<Float>,
    onDepositClick: () -> Unit,
    onP2PClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = totalBalance,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    PriceChangeText(change = dailyChange)
                }
                // Sparkline Chart
                SparklineChart(
                    data = chartData,
                    modifier = Modifier
                        .height(50.dp)
                        .width(100.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onDepositClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Deposit")
                }
                Button(
                    onClick = onP2PClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("P2P Trading")
                }
            }
        }
    }
}

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

@Preview(showBackground = true)
@Composable
private fun BalanceSummaryCardPreview() {
    BalanceSummaryCard(
        totalBalance = "$3,462.10",
        dailyChange = 85.28,
        chartData = listOf(5f, 4f, 6f, 8f, 7f, 9f, 8f),
        onDepositClick = {},
        onP2PClick = {}
    )
}
