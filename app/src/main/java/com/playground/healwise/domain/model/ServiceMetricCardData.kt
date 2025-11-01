package com.playground.healwise.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ServiceMetricCardData(
    val title: String,
    val metricValue: String,
    val subtitle: String,
    val leadingIcon: ImageVector,
    val metricTrailingIcon: ImageVector,
    val backgroundColor: Color
)
