package com.playground.oppo.domain.model

import androidx.compose.ui.graphics.Color

data class DayPerformanceData(
    val day: DayOfWeek,
    val color: Color,
    val weight: Float = 1f,
    val isSelected: Boolean = false
)
