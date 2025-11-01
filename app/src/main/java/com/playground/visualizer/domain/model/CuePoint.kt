package com.playground.visualizer.domain.model

import androidx.compose.ui.geometry.Offset

data class CuePoint(
    val id: Int,
    val timestamp: Long,
    val position: Offset
)
