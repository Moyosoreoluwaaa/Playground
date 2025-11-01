package com.playground.rooms.presentation.uistate

import com.playground.rooms.domain.model.RoomsLegalEvent

data class RoomsProfileSetupUiState(
    val currentPhotoUrl: String? = null,
    val isUploading: Boolean = false,
    val isCompletingSetup: Boolean = false,
    val uploadError: String? = null,
    val setupError: String? = null,
)
