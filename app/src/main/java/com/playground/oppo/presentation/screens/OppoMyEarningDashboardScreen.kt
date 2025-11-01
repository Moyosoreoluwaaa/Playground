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
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.healwise.presentation.components.MaxAppBar
import com.playground.oppo.domain.model.KeyMetricData
import com.playground.oppo.domain.model.PrimaryMetricCardData
import com.playground.oppo.presentation.components.OppoKeyMetricCard
import com.playground.oppo.presentation.components.OppoMetricCard
import com.playground.oppo.presentation.uistate.OppoMyEarningDashboardEvent
import com.playground.oppo.presentation.uistate.OppoMyEarningDashboardUiState

@Composable
fun OppoMyEarningDashboardScreen(
    uiState: OppoMyEarningDashboardUiState,
    onEvent: (OppoMyEarningDashboardEvent) -> Unit
) {
    val background = Color(0xFFF3F7F2) // Light Background from blueprint

    Scaffold(
//        topBar = { MaxAppBar(onMenuClick = { onEvent(OppoMyEarningDashboardEvent.OnMenuClick) }) },
        containerColor = background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // Earning Summary Block
            Text(
                uiState.screenTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$${"%,.0f".format(uiState.totalRevenue)}",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 80.sp, // Custom size for "Main Metrics"
                    fontWeight = FontWeight.Light, // Serif-like Light weight
                    lineHeight = 90.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                uiState.contextText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(32.dp))

            // Primary Metric Cards (Side by Side)
            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .heightIn(min = 180.dp), // Set min height for squareness
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OppoMetricCard(
                    metricData = uiState.organicReachCard,
                    onClick = { onEvent(OppoMyEarningDashboardEvent.OnPrimaryCardClick(it)) },
                    modifier = Modifier.weight(1f)
                )
                OppoMetricCard(
                    metricData = uiState.overallSalesCard,
                    onClick = { onEvent(OppoMyEarningDashboardEvent.OnPrimaryCardClick(it)) },
                    onActionClick = { onEvent(OppoMyEarningDashboardEvent.OnSalesDrillDownClick) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(32.dp))

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
                        .height(100.dp), // Fixed height for key metric cards
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    uiState.keyMetrics.take(2).forEach { metric ->
                        OppoKeyMetricCard(
                            keyMetricData = metric,
                            onClick = { onEvent(OppoMyEarningDashboardEvent.OnKeyMetricClick(it)) },
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
private fun PreviewOppoMyEarningDashboardScreen() {
    val mockUiState = OppoMyEarningDashboardUiState.mock()

    MaterialTheme {
        OppoMyEarningDashboardScreen(
            uiState = mockUiState,
            onEvent = {} // No-op for preview
        )
    }
}
