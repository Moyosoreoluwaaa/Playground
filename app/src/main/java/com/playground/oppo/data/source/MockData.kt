package com.playground.oppo.data.source

import androidx.compose.ui.graphics.Color
import com.playground.oppo.domain.model.KeyMetricData
import com.playground.oppo.domain.model.PrimaryMetricCardData
import com.playground.oppo.presentation.uistate.OppoMyEarningDashboardUiState

val MockOppoMyEarningDashboardUiState = OppoMyEarningDashboardUiState(
    organicReachCard = PrimaryMetricCardData(
        id = "reach", primaryMetric = "50.2K", description = "Organic Reach",
        iconRes = 1, actionIconRes = null, // Placeholder for icon
        backgroundColor = Color(0xFFFF4500), textColor = Color.White // Bright Orange
    ),
    overallSalesCard = PrimaryMetricCardData(
        id = "sales", primaryMetric = "", description = "Overall Sales",
        secondaryMetric = "30M",
        iconRes = null, actionIconRes = 2, // Placeholder for action arrow
        backgroundColor = Color(0xFFF5E3F8), textColor = Color.Black
    ),
    keyMetrics = listOf(
        KeyMetricData(id = "conv", metricValue = "4.8%", description = "Conversion Rate"),
        KeyMetricData(
            id = "sess",
            metricValue = "3:45 Min",
            description = "Avg Session",
            leadingIconColor = Color.Black
        ),
    )
)