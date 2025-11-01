package com.playground.oppo.domain.model

data class AnalyticsCardData(
    val title: String,
    val metricValue: String,
    val changeValue: String,
    val changeIsPositive: Boolean,
    val dailyPerformance: List<DayPerformanceData>
)
