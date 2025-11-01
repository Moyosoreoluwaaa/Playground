package com.playground.oakk.uistates

import com.playground.oakk.models.Product
import com.playground.oakk.models.Transaction

// Enums for UI state
enum class DashboardTab { DASHBOARD, INVESTMENTS, ANALYTICS }
enum class QuickAction { TOP_UP, INVEST, SEND_MONEY, PAY_BILLS }

// Main UI State data class
data class DashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val userName: String = "",
    val userAvatarUrl: String = "",
    val selectedTab: DashboardTab = DashboardTab.DASHBOARD,
    val totalBalance: String = "",
    val monthlyPerformance: String = "",
    val products: List<Product> = emptyList(),
    val transactions: Map<String, List<Transaction>> = emptyMap() // Key: Month name
)