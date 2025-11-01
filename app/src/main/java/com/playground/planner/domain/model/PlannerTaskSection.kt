package com.playground.planner.domain.model

data class PlannerTaskSection(
    val title: String, // e.g., "TODO"
    val tasks: List<PlannerTask>
)
