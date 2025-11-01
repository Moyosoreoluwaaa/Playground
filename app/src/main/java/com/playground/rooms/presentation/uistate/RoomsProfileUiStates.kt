package com.playground.rooms.presentation.uistate

data class RoomsProfileUiState(
    val avatarUrl: String? = "https://i.pravatar.cc/150?img=12",
    val name: String = "Anna Scott",
    val handle: String = "@anna_scott",
    val email: String = "anna.scott@email.com",
    val bio: String = "Designer, traveler, and coffee enthusiast.",
    val isSaving: Boolean = false,
    val hasChanges: Boolean = true, // Set to true for previewing enabled state
    val error: String? = null
)

sealed class RoomsProfileEvent {
    data object BackClicked : RoomsProfileEvent()
    data object EditAvatarClicked : RoomsProfileEvent()
    data object AccountSettingsClicked : RoomsProfileEvent()
    data object EditUsernameClicked : RoomsProfileEvent()
    data object EditEmailClicked : RoomsProfileEvent()
    data object EditBioClicked : RoomsProfileEvent()
    data object NotificationSettingsClicked : RoomsProfileEvent()
    data object SaveChangesClicked : RoomsProfileEvent()
    data object LogoutClicked : RoomsProfileEvent()
}
