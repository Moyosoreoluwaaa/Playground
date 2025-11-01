package com.playground.oppo.presentation.uistate

import androidx.compose.ui.graphics.Color
import com.playground.oppo.domain.model.KeyMetricData
import com.playground.oppo.domain.model.PrimaryMetricCardData

data class OppoMyEarningDashboardUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val screenTitle: String = "My Earning",
    val totalRevenue: Double = 124500.0,
    val contextText: String = "Total Revenue",
    val organicReachCard: PrimaryMetricCardData,
    val overallSalesCard: PrimaryMetricCardData,
    val keyMetrics: List<KeyMetricData>
) {
    companion object {
        val mockOrange = Color(0xFFFF4500)
        val mockPink = Color(0xFFF5E3F8)
        val mockGreen = Color(0xFF4CAF50)

        fun mock() = OppoMyEarningDashboardUiState(
            organicReachCard = PrimaryMetricCardData(
                id = "reach", primaryMetric = "50.2K", description = "Organic Reach",
                iconRes = 1, actionIconRes = null, // Placeholder for icon
                backgroundColor = mockOrange, textColor = Color.White
            ),
            overallSalesCard = PrimaryMetricCardData(
                id = "sales", primaryMetric = "", description = "Overall Sales",
                secondaryMetric = "30M",
                iconRes = null, actionIconRes = 2, // Placeholder for action arrow
                backgroundColor = mockPink, textColor = Color.Black
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
    }
}

sealed class OppoMyEarningDashboardEvent {
    data object OnMenuClick : OppoMyEarningDashboardEvent()
    data class OnPrimaryCardClick(val metricId: String) : OppoMyEarningDashboardEvent()
    data object OnSalesDrillDownClick : OppoMyEarningDashboardEvent() // Specific for the arrow
    data class OnKeyMetricClick(val metricId: String) : OppoMyEarningDashboardEvent()
}
