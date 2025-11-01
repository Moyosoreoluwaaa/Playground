package com.playground.rooms.domain.model

import androidx.compose.ui.graphics.Color

data class Message(
    val id: String,
    val senderId: String,
    val content: String,
    val timestamp: String,
    val senderAvatarUrl: String? = null,
    val senderAvatarColor: Color = Color.Gray
)
