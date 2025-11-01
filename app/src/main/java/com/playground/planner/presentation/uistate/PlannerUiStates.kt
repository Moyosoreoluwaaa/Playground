package com.playground.planner.presentation.uistate

import com.playground.planner.domain.model.PlannerIntervalType
import com.playground.planner.domain.model.PlannerTaskSection
import com.playground.planner.domain.model.PlannerViewType

data class PlannerUiState(
    val isLoading: Boolean = false,
    val projectName: String = "Event Planning",
    val currentView: PlannerViewType = PlannerViewType.TIMELINE,
    val currentSegmentFilter: PlannerIntervalType = PlannerIntervalType.WEEKLY_PLAN,
    val currentNavigationLabel: String = "Today",
    val notificationCount: Int = 1,
    val avatarUrl: String? = "https://i.pravatar.cc/150?img=1", // Mock URL
    val taskSections: List<PlannerTaskSection> = emptyList()
)
