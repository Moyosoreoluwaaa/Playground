package com.playground.oppo.domain.model

import androidx.compose.ui.graphics.Color

data class MetricCardData(
    val id: String,
    val primaryMetric: String, // e.g., "50.2K" or "1.82M"
    val description: String, // e.g., "Active Users"
    val iconRes: Int?, // For the small icon (optional)
    val actionIconRes: Int?, // For the small corner action (optional)
    val backgroundColor: Color,
    val textColor: Color
)
