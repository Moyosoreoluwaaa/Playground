package com.playground.rooms.presentation.uistate

import com.playground.rooms.domain.model.RoomsLegalEvent

data class RoomsNotificationUiState(
    val isProcessing: Boolean = false,
    val hasPermission: Boolean = false,
    val error: String? = null
)
