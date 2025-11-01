package com.playground.crypto.presentation.uistate

import com.playground.crypto.domain.model.Coin
import com.playground.crypto.domain.model.WatchlistFilter

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val totalBalance: String = "",
    val dailyChange: Double = 0.0,
    val balanceChartData: List<Float> = emptyList(),
    val selectedFilter: WatchlistFilter = WatchlistFilter.HOT,
    val watchlist: List<Coin> = emptyList()
)
