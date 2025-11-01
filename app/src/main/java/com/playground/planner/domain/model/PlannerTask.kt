package com.playground.planner.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class PlannerTask(
    val id: String,
    val title: String,
    val priority: PlannerPriority,
    val statusTag: String,
    val timeRemaining: String,
    val typeIcon: ImageVector,
    val linkCount: Int,
    val commentCount: Int,
    val isOnline: Boolean = false
)
