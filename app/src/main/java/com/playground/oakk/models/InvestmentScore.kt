package com.playground.oakk.models


// Represents a score shown in the UI
data class InvestmentScore(
    val value: Int = 0,
    val change: Int = 0
)

// Main UI State for the Investments screen
data class InvestmentsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val diversificationScore: Int = 0,
    val netWorthScore: InvestmentScore = InvestmentScore(),
    val performanceMessage: String = "",
    val assetCategories: List<AssetCategory> = emptyList()
)