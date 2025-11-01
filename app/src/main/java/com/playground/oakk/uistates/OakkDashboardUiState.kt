package com.playground.oakk.uistates

import androidx.compose.ui.graphics.Color

// UiState
data class OakkDashboardUiState(
    val userName: String = "",
    val selectedTab: TabType = TabType.INVESTMENTS,
    val netWorth: String = "",
    val netWorthChange: String = "",
    val monthlyData: List<OakkMonthData> = emptyList(),
    val assetCategories: List<OakkAssetCategory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// Domain Models & Enums
enum class TabType {
    DASHBOARD, INVESTMENTS, ANALYTICS
}

data class OakkMonthData(
    val month: String,
    val segments: List<OakkSegment>
)

data class OakkSegment(
    val categoryName: String,
    val value: Double,
    val color: Color
)

data class OakkAssetCategory(
    val name: String,
    val percentage: String,
    val color: Color
)