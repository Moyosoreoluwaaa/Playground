package com.playground.healwise.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.healwise.data.source.MockUiState
import com.playground.healwise.presentation.components.HealwiseAppBar
import com.playground.healwise.presentation.components.PatientStatsCard
import com.playground.healwise.presentation.components.ServiceMetricCard
import com.playground.healwise.presentation.uistate.DashboardEvent
import com.playground.healwise.presentation.uistate.DashboardUiState

@Composable
fun HealwiseDashboardScreen(state: DashboardUiState, onEvent: (DashboardEvent) -> Unit) {
    Scaffold(
        topBar = { HealwiseAppBar(onMenuToggled = { onEvent(DashboardEvent.OnMenuToggled) }) },
        containerColor = Color(0xFFF3F7F2) // Light Green/Gray Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // KPI Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = true,
                    onClick = { onEvent(DashboardEvent.OnPeriodFilterClick) },
                    label = { Text(state.premiumLabel) },
                    leadingIcon = { Icon(state.premiumIcon, null, Modifier.size(18.dp)) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = Color(0xFFF3F7F2))
                )
                TextButton(onClick = { onEvent(DashboardEvent.OnPeriodFilterClick) }) {
                    Text("Last Week Data")
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowUpward, contentDescription = null)
                }
            }
            Spacer(Modifier.height(32.dp))

            Column (Modifier.fillMaxWidth()) {
                // Main Growth Metric
                Text(
                    state.growthPercentage,
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 96.sp),
                    textAlign = TextAlign.Center
                )
                Text(state.growthDescription,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Right
                )
            }

            Spacer(Modifier.height(32.dp))

            // Service Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ServiceMetricCard(
                    state.packageCard,
                    onClick = { onEvent(DashboardEvent.OnPackageCardClick) },
                    modifier = Modifier.weight(1f)
                )
                ServiceMetricCard(
                    state.labResultsCard,
                    onClick = { onEvent(DashboardEvent.OnLabResultsCardClick) },
                    modifier = Modifier.weight(1f)
                )
            }
//            Spacer(Modifier.height(16.dp))

            // Active Patients
            PatientStatsCard(
                stats = state.patientStats,
                onDetailsClick = { onEvent(DashboardEvent.OnPatientStatsDetailsClick) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    // Assuming a simplified M3 theme for the preview
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color(0xFFF3F7F2),
            onSurfaceVariant = Color(0xFF333333) // Custom color for venn diagram text
        )
    ) {
        HealwiseDashboardScreen(
            state = MockUiState,
            onEvent = {}
        )
    }
}
