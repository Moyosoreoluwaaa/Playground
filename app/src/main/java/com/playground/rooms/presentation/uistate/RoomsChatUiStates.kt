package com.playground.rooms.presentation.uistate

import androidx.compose.ui.graphics.Color
import com.playground.rooms.domain.model.Message

data class RoomsChatUiState(
    val roomId: String = "design_critique",
    val roomName: String = "Daily Design Critiques",
    val messages: List<Message> = emptyList(),
    val messageDraft: String = "",
    val currentUserId: String = "user_456", // Defined as current user
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class RoomsChatEvent {
    data object BackClicked : RoomsChatEvent()
    data class MessageDraftChanged(val draft: String) : RoomsChatEvent()
    data object SendClicked : RoomsChatEvent()
    data object LoadMoreMessages : RoomsChatEvent()
}
