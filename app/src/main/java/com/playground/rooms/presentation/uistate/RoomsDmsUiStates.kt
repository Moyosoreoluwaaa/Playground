package com.playground.rooms.presentation.uistate

import com.playground.rooms.domain.model.Conversation
import com.playground.rooms.domain.model.MainNavTab

data class RoomsDmsUiState(
    val currentSearchQuery: String = "",
    val conversations: List<Conversation> = emptyList(),
    val currentTab: MainNavTab = MainNavTab.DMS,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class RoomsDmsEvent {
    data object NewMessageClicked : RoomsDmsEvent()
    data class SearchQueryChanged(val query: String) : RoomsDmsEvent()
    data class ConversationSelected(val conversationId: String) : RoomsDmsEvent()
    data class TabSelected(val tab: MainNavTab) : RoomsDmsEvent()
}
