package com.playground.oppo.presentation.uistate

import com.playground.oppo.domain.model.AnalyticsCardData
import com.playground.oppo.domain.model.DayOfWeek

data class OppoSalesAnalyticsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val promotionText: String = "50% Super Cyber Monday Sale",
    val totalSalesValue: Double = 92330.0,
    val contextText: String = "In Total Global",
    val analyticsCardData: AnalyticsCardData
)

sealed class OppoSalesAnalyticsEvent {
    data object OnMenuClick : OppoSalesAnalyticsEvent()
    data object OnSettingsClick : OppoSalesAnalyticsEvent()
    data object OnMaximizeClick : OppoSalesAnalyticsEvent()
    data class OnDaySelected(val day: DayOfWeek) : OppoSalesAnalyticsEvent() // Tap on a colored daily block
    data object OnCardClick : OppoSalesAnalyticsEvent() // Optional full card click
    data object OnRetryLoad : OppoSalesAnalyticsEvent()
}
