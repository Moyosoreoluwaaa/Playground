package com.playground.oppo.domain.model

import androidx.compose.ui.graphics.Color

data class CompositeBlockData(
    val id: String,
    val mainValue: String, // e.g., "83"
    val detailLine1: String, // e.g., "Ads — Live"
    val detailLine2: String, // e.g., "4.8K Plus Leads"
    val sponsoredText: String, // e.g., "Sponsored <"
    val backgroundColor: Color,
    val textColor: Color
)
