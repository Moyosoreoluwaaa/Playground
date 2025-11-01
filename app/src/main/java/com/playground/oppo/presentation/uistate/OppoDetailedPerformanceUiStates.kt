package com.playground.oppo.presentation.uistate

import androidx.compose.ui.graphics.Color
import com.playground.oppo.domain.model.ChartData
import com.playground.oppo.domain.model.ChartPoint
import com.playground.oppo.domain.model.ChartSeries
import com.playground.oppo.domain.model.KeyMetricData

sealed class OppoDetailedPerformanceEvent {
    data object OnMenuClick : OppoDetailedPerformanceEvent()
    data class OnChartInteraction(val x: Float, val y: Float) :
        OppoDetailedPerformanceEvent() // For chart tooltip

    data class OnKeyMetricClick(val metricId: String) : OppoDetailedPerformanceEvent()
}

data class OppoDetailedPerformanceUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val screenTitle: String = "Detailed Performance",
    val chartData: ChartData,
    val keyMetrics: List<KeyMetricData>
) {
    companion object {
        val OrangeSales = Color(0xFFFF4500)
        val PaidAds = Color(0xFFD81B60)
        val PaidAdsArea = Color(0xFFD5F3D1)
        val ChartAreaGradientEnd = Color(0xFFFFC080)

        fun mock() = OppoDetailedPerformanceUiState(
            chartData = ChartData(
                points = listOf(
                    ChartPoint("Ma", 80f, 50f),
                    ChartPoint("4n", 90f, 60f),
                    ChartPoint("10n", 40f, 55f),
                    ChartPoint("00", 95f, 65f),
                    ChartPoint("10S", 100f, 70f)
                ),
                series = listOf(
                    ChartSeries("Organic Sales", OrangeSales, true),
                    ChartSeries("Paid Ads", PaidAds, false),
                    ChartSeries(
                        "Paid Ads",
                        PaidAdsArea,
                        true
                    ) // Representing the colored segment on X-axis
                )
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
