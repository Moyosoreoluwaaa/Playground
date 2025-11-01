package com.playground.rooms.domain.model

import androidx.compose.ui.graphics.Color

data class Room(
    val id: String,
    val name: String,
    val subtitle: String,
    val avatarUrl: String?,
    val roomColor: Color
)
