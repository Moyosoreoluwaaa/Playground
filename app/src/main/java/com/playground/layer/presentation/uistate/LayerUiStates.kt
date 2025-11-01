package com.playground.layer.presentation.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import com.playground.layer.domain.model.AppTab
import com.playground.layer.domain.model.Asset
import com.playground.layer.domain.model.BottomNavDestination
import com.playground.layer.domain.model.DApp
import com.playground.layer.domain.model.FeeLevel
import com.playground.layer.domain.model.HistoryEntry
import com.playground.layer.domain.model.Interval
import com.playground.layer.domain.model.NftAppTab
import com.playground.layer.domain.model.NftItem
import com.playground.layer.domain.model.NftScreenState
import com.playground.layer.domain.model.NftTabType
import com.playground.layer.domain.model.ScreenState
import com.playground.layer.domain.model.TabType
import com.playground.layer.domain.model.Token

@Immutable
data class BrowserUiState(
    val screenState: ScreenState = ScreenState.READY,
    val searchQuery: String = "",
    val recommendedDApps: List<DApp> = emptyList(),
    val browsingHistory: List<HistoryEntry> = emptyList(),
)

sealed class BrowserEvent {
    data class onQueryChange(val query: String) : BrowserEvent()
    data class onNavigate(val url: String) : BrowserEvent()
    data class onDAppClicked(val dapp: DApp) : BrowserEvent()
    data class onHistoryItemClicked(val entry: HistoryEntry) : BrowserEvent()
}

data class ChartTooltip(
    val price: String,
    val date: String,
    val position: Offset // For drawing the tooltip box
)

