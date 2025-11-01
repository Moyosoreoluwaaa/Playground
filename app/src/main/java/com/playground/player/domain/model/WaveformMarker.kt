package com.playground.player.domain.model

import androidx.compose.ui.graphics.Color

data class WaveformMarker(
    val id: Int,
    val position: Float, // 0.0f to 1.0f
    val label: String,
    val color: Color
)
