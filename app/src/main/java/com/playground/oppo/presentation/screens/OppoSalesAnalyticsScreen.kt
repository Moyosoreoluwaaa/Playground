package com.playground.oppo.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.healwise.presentation.components.MaxAppBar
import com.playground.oppo.domain.model.AnalyticsCardData
import com.playground.oppo.domain.model.DayOfWeek
import com.playground.oppo.domain.model.DayPerformanceData
import com.playground.oppo.presentation.components.AnalyticsHeader
import com.playground.oppo.presentation.components.AnalyticsOrderCard
import com.playground.oppo.presentation.uistate.OppoSalesAnalyticsEvent
import com.playground.oppo.presentation.uistate.OppoSalesAnalyticsUiState
import java.util.Locale

// --- Screen Composable ---
@Composable
fun OppoSalesAnalyticsScreen(
    uiState: OppoSalesAnalyticsUiState,
    onEvent: (OppoSalesAnalyticsEvent) -> Unit
) {
    val background = Color(0xFFF3F7F2) // Light Background from blueprint

    Scaffold(
//        topBar = { MaxAppBar(onMenuClick = { onEvent(OppoSalesAnalyticsEvent.OnMenuClick) }) },
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

            // Sales Metric Block
            Text(
                uiState.promotionText.uppercase(Locale.ROOT),
                style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 2.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$${"%,.0f".format(uiState.totalSalesValue)}",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Light,
                    lineHeight = 96.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "\u223C", // Tilde separator
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                uiState.contextText,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(48.dp))

            // Data Analytics Header
            AnalyticsHeader(
                title = "Data Analytics",
                onSettingsClick = { onEvent(OppoSalesAnalyticsEvent.OnSettingsClick) },
                onMaximizeClick = { onEvent(OppoSalesAnalyticsEvent.OnMaximizeClick) }
            )

            Spacer(Modifier.height(16.dp))

            // Analytics Card
            AnalyticsOrderCard(
                cardData = uiState.analyticsCardData,
                onDaySelected = { onEvent(OppoSalesAnalyticsEvent.OnDaySelected(it)) }
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOppoSalesAnalyticsScreen() {
    // Mock Data
    val dailyData = listOf(
        DayPerformanceData(DayOfWeek.S, Color(0xFFFF4500)),
        DayPerformanceData(DayOfWeek.M, Color(0xFFD5F3D1)),
        DayPerformanceData(DayOfWeek.T, Color(0xFFF5E3F8)),
        DayPerformanceData(DayOfWeek.W, Color(0xFFFF4500)),
        DayPerformanceData(DayOfWeek.TH, Color(0xFFF5E3F8)),
        DayPerformanceData(DayOfWeek.F, Color(0xFFD5F3D1)),
        DayPerformanceData(DayOfWeek.SA, Color(0xFFD5F3D1))
    )
    val mockCardData = AnalyticsCardData(
        title = "Average Order",
        metricValue = "$1,238.96 USD",
        changeValue = "+2.75^\uFE0E",
        changeIsPositive = true,
        dailyPerformance = dailyData
    )
    val mockUiState = OppoSalesAnalyticsUiState(analyticsCardData = mockCardData)

    MaterialTheme {
        OppoSalesAnalyticsScreen(
            uiState = mockUiState,
            onEvent = {} // No-op for preview
        )
    }
}
