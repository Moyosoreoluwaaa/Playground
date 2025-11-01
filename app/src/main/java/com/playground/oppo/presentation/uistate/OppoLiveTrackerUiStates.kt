package com.playground.oppo.presentation.uistate

import androidx.compose.ui.graphics.Color
import com.playground.oppo.domain.model.CompositeBlockData
import com.playground.oppo.domain.model.MetricCardData

data class OppoLiveTrackerUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val title: String = "Track Your Earning Live Data.",
    val growthPercentage: Double = 24.0,
    val growthDescription: String = "Growth From Yesterday",
    val chartData: List<Float> = emptyList(),
    val activeUsersCard: MetricCardData,
    val overallSalesCard: MetricCardData,
    val adsLeadsBlock: CompositeBlockData
) {
    companion object {
        fun mock() = OppoLiveTrackerUiState(
            activeUsersCard = MetricCardData(
                id = "users", primaryMetric = "50.2K", description = "Active Users",
                iconRes = null, actionIconRes = null,
                backgroundColor = Color(0xFFFF4500), textColor = Color.White // Bright Orange
            ),
            overallSalesCard = MetricCardData(
                id = "sales", primaryMetric = "1.82M", description = "Overall Sales",
                iconRes = null, actionIconRes = null,
                backgroundColor = Color(0xFFF5E3F8), textColor = Color.Black // Light Pink
            ),
            adsLeadsBlock = CompositeBlockData(
                id = "leads", mainValue = "83—", detailLine1 = "Ads — Live",
                detailLine2 = "4.8K Plus Leads", sponsoredText = "Sponsored <",
                backgroundColor = Color(0xFFD5F3D1), textColor = Color.Black // Lime Green
            )
        )
    }
}

sealed class OppoLiveTrackerEvent {
    data object OnMenuClick : OppoLiveTrackerEvent()
    data object OnGrowthMetricClick : OppoLiveTrackerEvent()
    data class OnMetricCardClick(val metricId: String) : OppoLiveTrackerEvent()
    data object OnSalesDrillDownClick : OppoLiveTrackerEvent()
    data object OnSponsoredLinkClick : OppoLiveTrackerEvent()
}
