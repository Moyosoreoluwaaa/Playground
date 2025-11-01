package com.playground.oppo.domain.model

import androidx.compose.ui.graphics.Color

data class PrimaryMetricCardData(
    val id: String,
    val primaryMetric: String,
    val description: String,
    val secondaryMetric: String = "",
    val iconRes: Int?,
    val actionIconRes: Int?,
    val backgroundColor: Color,
    val textColor: Color
)
