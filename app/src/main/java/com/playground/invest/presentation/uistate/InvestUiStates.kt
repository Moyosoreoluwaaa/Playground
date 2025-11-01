package com.playground.invest.presentation.uistate

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.ui.graphics.vector.ImageVector

// Top-level UI state for the dashboard screen
data class InvestDashboardUiState(
    val userName: String = "Aleksandr Kosov",
    val totalBalance: String = "$ 804,660.00",
    val balanceChangePercentage: String = "+13.00%",
    val isLoading: Boolean = false,
    val error: String? = null,
    val quickActions: List<InvestQuickAction> = emptyList(),
    val products: List<InvestProduct> = emptyList(),
    val recentTransactions: List<InvestTransaction> = emptyList()
)

// Represents a product card in the "Products" section
data class InvestProduct(
    val id: Int,
    val imageUrl: String,
    val title: String,
    val price: String
)

// Represents a transaction item in the "Transactions" list
data class InvestTransaction(
    val id: Int,
    val imageUrl: String,
    val title: String,
    val subtitle: String,
    val amount: String,
    val isDebit: Boolean
)

// Enum for quick action buttons
enum class InvestQuickAction(val label: String, val icon: ImageVector) {
    INVEST("Invest", Icons.Default.ArrowUpward),
    SEND_MONEY("Send Money", Icons.AutoMirrored.Filled.Send),
    PAY_BILLS("Pay Bills", Icons.AutoMirrored.Filled.List)
}

data class PortfolioItem(
    val id: Int,
    val name: String,
    val ticker: String,
    val logoUrl: String,
    val price: String,
    val change: String,
    val isFavorite: Boolean
)

data class Transaction(
    val id: Int,
    val name: String,
    val description: String,
    val amount: String,
    val iconUrl: String,
    val isDebit: Boolean
)

data class QuickAction(
    val id: Int,
    val icon: ImageVector,
    val label: String
)
