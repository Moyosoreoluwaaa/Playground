package com.playground.oppo.domain.model

import androidx.compose.ui.graphics.Color

data class ActivityItemData(
    val id: String,
    val title: String,
    val iconRes: Int, // Placeholder for drawable resource ID
    val iconBackgroundColor: Color,
    val percentageMetric: String,
    val monetaryMetric: String,
    val timestamp: String
)
