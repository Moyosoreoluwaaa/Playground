package com.playground.layer

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.KeyboardType
import com.playground.layer.domain.model.DApp
import com.playground.layer.domain.model.HistoryEntry
import com.playground.layer.domain.model.ScreenState

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

