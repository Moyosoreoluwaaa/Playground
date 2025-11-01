package com.playground.layer.presentation.uistate

import androidx.compose.runtime.Immutable
import com.playground.layer.domain.model.BottomNavDestination
import com.playground.layer.domain.model.TabType
import com.playground.layer.domain.model.Token

sealed interface PortfolioOverviewEvent {
    data class TabSelected(val type: TabType) : PortfolioOverviewEvent
    data class TokenClicked(val token: Token) : PortfolioOverviewEvent
    data class TokenMoreOptionsClicked(val token: Token) : PortfolioOverviewEvent
    data class BottomNavSelected(val destination: BottomNavDestination) : PortfolioOverviewEvent
    data object RefreshPortfolioData : PortfolioOverviewEvent
}

data class PortfolioOverviewUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val totalBalance: String = "$5,832.50",
    val dailyPercentageChange: String = "+4.5%",
    val isDailyChangePositive: Boolean = true,
    val selectedTab: TabType = TabType.TOKENS,
    val selectedBottomDestination: BottomNavDestination = BottomNavDestination.ACCOUNT,
    val tokenList: List<Token> = emptyList()
)
