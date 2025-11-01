package com.playground.oppo.domain.model

import androidx.compose.ui.graphics.Color

data class KeyMetricData(
    val id: String,
    val metricValue: String,
    val description: String,
    val leadingIconColor: Color = Color.Black
)
