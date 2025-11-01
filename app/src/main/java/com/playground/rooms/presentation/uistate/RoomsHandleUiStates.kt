package com.playground.rooms.presentation.uistate

import com.playground.rooms.domain.model.HandleValidationState
import com.playground.rooms.domain.model.RoomsLegalEvent

data class RoomsHandleUiState(
    val currentHandle: String = "user_handle123",
    val validationState: HandleValidationState = HandleValidationState.IDLE,
    val isConfirmEnabled: Boolean = validationState == HandleValidationState.VALID,
    val isScreenLoading: Boolean = false,
    val error: String? = null
)
