package com.playground.layer.presentation.uistate

import androidx.compose.ui.geometry.Offset
import com.playground.layer.domain.model.AppTab
import com.playground.layer.domain.model.Asset
import com.playground.layer.domain.model.Interval
import com.playground.layer.domain.model.ScreenState

data class AssetDetailUiState(
    val screenState: ScreenState = ScreenState.LOADING,
    val asset: Asset = Asset(ticker = "BTC", name = "Bitcoin", iconUrl = "bitcoin_icon_url"),

    // Price State
    val currentPrice: String = "$0.00",
    val priceChangePercent: String = "+0.0%",
    val isPricePositive: Boolean = true,

    // Chart State
    val chartData: List<Float> = emptyList(), // Normalized price points
    val selectedInterval: Interval = Interval.ONE_DAY,
    val currentTooltip: ChartTooltip? = null, // Null when not dragging

    // Stats State
    val balanceBTC: String = "0.0 BTC",
    val balanceUSD: String = "$0.00",
    val marketCap: String = "$0.00",
    val marketCapRank: String = "#0",
    val volume24H: String = "$0.00",
    val circulatingSupply: String = "0.0",

    // UI Controls
    val currentSelectedTab: AppTab = AppTab.ACCOUNT
)

sealed class AssetDetailEvent {
    data object onBackClicked : AssetDetailEvent()
    data class onIntervalChanged(val interval: Interval) : AssetDetailEvent()
    data class onChartIndicatorMove(val position: Offset) : AssetDetailEvent()
    data object onSwapClicked : AssetDetailEvent()
    data object onBuyClicked : AssetDetailEvent()
    data class onTabSelected(val tab: AppTab) : AssetDetailEvent()
}
