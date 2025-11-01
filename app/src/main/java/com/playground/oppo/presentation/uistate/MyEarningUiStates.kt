package com.playground.oppo.presentation.uistate

import com.playground.oppo.domain.model.EarningSource

data class MyEarningUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val totalEarnings: Double = 45670.0,
    val dateRangeText: String = "28 May – 04 June",
    val sources: List<EarningSource> = emptyList(),
    val disclaimer: String = "*Data are bases on selective range point"
)
sealed class MyEarningEvent {
    data object OnMenuClick : MyEarningEvent()
    data object OnMyAccountClick : MyEarningEvent()
    data object OnDateRangeClick : MyEarningEvent()
    data class OnSourceClick(val sourceId: String) : MyEarningEvent()
}
