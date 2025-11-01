package com.playground.rooms.domain.model

import androidx.compose.ui.graphics.Color

data class Conversation(
    val id: String,
    val title: String,
    val lastMessage: String,
    val timestamp: String,
    val isUnread: Boolean,
    val avatarUrl: String?,
    val isGroup: Boolean,
    val avatarTint: Color = Color.Gray // For default avatar background
)
