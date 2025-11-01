package com.playground.oppo.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.healwise.presentation.components.MaxAppBar
import com.playground.oppo.presentation.components.LiveChartCanvas
import com.playground.oppo.presentation.components.OppoCompositeBlock
import com.playground.oppo.presentation.components.OppoGenericMetricCard
import com.playground.oppo.presentation.uistate.OppoLiveTrackerEvent
import com.playground.oppo.presentation.uistate.OppoLiveTrackerUiState

// --- Screen Composable ---
@Composable
fun OppoLiveTrackerScreen(
    uiState: OppoLiveTrackerUiState,
    onEvent: (OppoLiveTrackerEvent) -> Unit
) {
    val background = Color(0xFFF3F7F2) // Light Background from blueprint

    Scaffold(
//        topBar = { MaxAppBar(onMenuClick = { onEvent(OppoLiveTrackerEvent.OnMenuClick) }) },
        containerColor = background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            // Title and Growth Metric Block
            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Title
                Text(
                    uiState.title,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Light, // Serif-like Light weight
                        lineHeight = 60.sp
                    ),
                )

                // Growth Text
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    Text(
                        "${uiState.growthPercentage.toInt()}^\uFE0E",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        uiState.growthDescription,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End
                    )
                }
            }

            // Chart Placeholder
            LiveChartCanvas(modifier = Modifier.padding(vertical = 8.dp), data = uiState.chartData)

            Spacer(Modifier.height(24.dp))

            // Top Metric Cards (Side by Side)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OppoGenericMetricCard(
                    metricData = uiState.activeUsersCard,
                    onClick = { onEvent(OppoLiveTrackerEvent.OnMetricCardClick(it)) },
                    modifier = Modifier.weight(1f)
                )
                OppoGenericMetricCard(
                    metricData = uiState.overallSalesCard,
                    onClick = { onEvent(OppoLiveTrackerEvent.OnMetricCardClick(it)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Bottom Composite Block
            OppoCompositeBlock(
                compositeData = uiState.adsLeadsBlock,
                onSponsoredClick = { onEvent(OppoLiveTrackerEvent.OnSponsoredLinkClick) },
                onClick = { onEvent(OppoLiveTrackerEvent.OnMetricCardClick(it)) }
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOppoLiveTrackerScreen() {
    MaterialTheme {
        // Use the mock data defined in the UiState companion object
        OppoLiveTrackerScreen(
            uiState = OppoLiveTrackerUiState.mock().copy(chartData = listOf(0.1f, 0.5f, 0.2f, 0.8f)),
            onEvent = {}
        )
    }
}
