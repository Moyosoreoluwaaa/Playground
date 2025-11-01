package com.playground.crypto.presentation.uistate

import android.graphics.PointF
import com.playground.crypto.domain.model.ChartInterval
import com.playground.crypto.domain.model.Coin
import com.playground.crypto.domain.model.OrderBook
import com.playground.crypto.domain.model.OrderBookTab

data class CoinDetailUitate(
    val isLoading: Boolean = true,
    val error: String? = null,
    val coin: Coin? = null,
    val isFavorite: Boolean = false,
    val selectedInterval: ChartInterval = ChartInterval.H24,
    val chartData: List<PointF> = emptyList(),
    val selectedOrderBookTab: OrderBookTab = OrderBookTab.ORDER_BOOK,
    val orderBook: OrderBook? = null,
    // Add states for History and Info tabs as needed
)
