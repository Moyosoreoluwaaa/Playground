package com.playground.language.data

// LangMetric (from Dashboard) is too generic. Using a specific one for this summary.
data class LangAchievementMetric(
    val id: String,
    val label: String,
    val value: String,
    val type: LangMetricType
)

enum class LangMetricType {
    STREAK,
    TIME_SPENT,
    BADGE,
    // Add more types as needed
}