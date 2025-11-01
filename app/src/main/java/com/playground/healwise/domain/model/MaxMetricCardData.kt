package com.playground.healwise.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class MaxMetricCardData(
    val type: MaxMetricType,
    val icon: ImageVector,
    val metricValue: String, // e.g., "2.96M"
    val description: String, // e.g., "Patients Seen"
    val subtext: String,     // e.g., "Globally—"
    val backgroundColor: Color
)
