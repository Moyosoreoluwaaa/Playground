package com.playground.rooms.presentation.uistate

import com.playground.rooms.domain.model.NotificationSetting
import com.playground.rooms.domain.model.NotificationType

data class RoomsNotificationSettingsUiState(
    val settings: List<NotificationSetting> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class RoomsNotificationSettingsEvent {
    data object BackClicked : RoomsNotificationSettingsEvent()
    data class ToggleSetting(val type: NotificationType, val isEnabled: Boolean) : RoomsNotificationSettingsEvent()
}
