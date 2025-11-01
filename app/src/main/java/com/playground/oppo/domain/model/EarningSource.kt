package com.playground.oppo.domain.model

import androidx.compose.ui.graphics.Color

data class EarningSource(
    val id: String,
    val type: MyEarningSourceType,
    val title: String,
    val iconColor: Color,
    val backgroundColor: Color,
    val percentageChange: Double,
    val amount: Double,
    val timestamp: String,
    val isPositiveChange: Boolean
)
