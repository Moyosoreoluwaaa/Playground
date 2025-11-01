package com.playground.healwise.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class StackedMetricData(
    val metricValue: String,
    val description: String,
    val icon: ImageVector,
    val backgroundColor: Color
)
