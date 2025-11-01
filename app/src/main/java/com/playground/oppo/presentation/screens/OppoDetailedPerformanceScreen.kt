package com.playground.oppo.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.healwise.presentation.components.MaxAppBar
import com.playground.oppo.presentation.components.ChartLegend
import com.playground.oppo.presentation.components.OppoKeyMetricCard
import com.playground.oppo.presentation.components.OppoPerformanceChart
import com.playground.oppo.presentation.uistate.OppoDetailedPerformanceEvent
import com.playground.oppo.presentation.uistate.OppoDetailedPerformanceUiState

// --- Screen Composable ---
@Composable
fun OppoDetailedPerformanceScreen(
    uiState: OppoDetailedPerformanceUiState,
    onEvent: (OppoDetailedPerformanceEvent) -> Unit
) {
    val background = Color(0xFFF3F7F2)

    Scaffold(
//        topBar = { MaxAppBar(onMenuClick = { onEvent(OppoDetailedPerformanceEvent.OnMenuClick) }) },
        containerColor = background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            // Title Block
            Text(
                uiState.screenTitle,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Light,
                    lineHeight = 60.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Total Revenue",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(24.dp))

            // Performance Chart
            OppoPerformanceChart(
                chartData = uiState.chartData,
                onPointSelected = { x, y ->
                    onEvent(
                        OppoDetailedPerformanceEvent.OnChartInteraction(
                            x,
                            y
                        )
                    )
                }
            )
            Spacer(Modifier.height(16.dp))

            // Chart Legend
            ChartLegend(series = uiState.chartData.series)

            Spacer(Modifier.height(40.dp))

            // Key Metrics Header
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Key Metrics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Key Metrics List (2-column layout)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    uiState.keyMetrics.take(2).forEach { metric ->
                        OppoKeyMetricCard(
                            keyMetricData = metric,
                            onClick = { onEvent(OppoDetailedPerformanceEvent.OnKeyMetricClick(it)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOppoDetailedPerformanceScreen() {
    MaterialTheme {
        OppoDetailedPerformanceScreen(
            uiState = OppoDetailedPerformanceUiState.mock(),
            onEvent = {}
        )
    }
}
