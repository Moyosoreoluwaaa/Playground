package com.playground.rooms.presentation.uistate

import com.playground.rooms.domain.model.MainNavTab
import com.playground.rooms.domain.model.Room

data class RoomsDashboardUiState(
    val currentSearchQuery: String = "",
    val rooms: List<Room> = emptyList(),
    val currentTab: MainNavTab = MainNavTab.ROOMS,
    val userProfileUrl: String? = "https://i.pravatar.cc/150?img=12",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class RoomsDashboardEvent {
    data object ProfileClicked : RoomsDashboardEvent()
    data class SearchQueryChanged(val query: String) : RoomsDashboardEvent()
    data object SearchClicked : RoomsDashboardEvent()
    data class RoomSelected(val roomId: String) : RoomsDashboardEvent()
    data class TabSelected(val tab: MainNavTab) : RoomsDashboardEvent()
}
