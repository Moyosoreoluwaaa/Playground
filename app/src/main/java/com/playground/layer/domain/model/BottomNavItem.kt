package com.playground.layer.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val destination: BottomNavDestination,
    val label: String,
    val icon: ImageVector
)