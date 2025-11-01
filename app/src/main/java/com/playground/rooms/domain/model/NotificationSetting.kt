package com.playground.rooms.domain.model

data class NotificationSetting(
    val type: NotificationType,
    val title: String,
    val description: String,
    val isEnabled: Boolean
)
