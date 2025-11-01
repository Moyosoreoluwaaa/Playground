package com.playground.healwise.domain.model

import androidx.compose.ui.graphics.Color

data class DiagramGroup(
    val valueFormatted: String, // The string to display e.g., "1.2M"
    val floatValue: Float,
    val color: Color
)
