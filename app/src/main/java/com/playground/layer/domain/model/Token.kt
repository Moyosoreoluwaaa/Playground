package com.playground.layer.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Token(
    val id: String,
    val name: String,
    val ticker: String,
    val iconUrl: String,
    val balanceTicker: String,
    val secondaryLabel: String,
    val priceSecondaryLabel: String,
    val percentageChange: Double,
    val isPositive: Boolean
)