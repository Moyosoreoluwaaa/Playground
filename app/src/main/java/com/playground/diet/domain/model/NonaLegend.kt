package com.playground.diet.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class NonaLegend(
    val type: NonaLegendType,
    val icon: ImageVector,
    val label: String,
    val color: Color,
    val isIcon: Boolean = true // Flag to distinguish icon vs. color block in legend
)
