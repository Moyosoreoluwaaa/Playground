package com.playground.healwise.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SingleMetricData(
    val metricValue: String,
    val subMetricValue: String? = null,
    val description: String,
    val icon: ImageVector,
    val backgroundColor: Color
)
