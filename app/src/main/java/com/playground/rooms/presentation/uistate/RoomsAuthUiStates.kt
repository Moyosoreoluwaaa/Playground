package com.playground.rooms.presentation.uistate

data class RoomsAuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val headline: String = "chat rooms with the most valuable",
    val avatarUrls: List<String> = listOf(
        "https://i.pravatar.cc/40?img=1", // Placeholder 1
        "https://i.pravatar.cc/40?img=5"  // Placeholder 2
    )
)
