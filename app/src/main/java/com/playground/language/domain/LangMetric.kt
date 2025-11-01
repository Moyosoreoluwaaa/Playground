package com.playground.language.domain

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

// --------------------------------------------------------------------------------
// 1. Domain Models
// --------------------------------------------------------------------------------

@Immutable
data class LangMetric(
    val id: String,
    @DrawableRes val iconResId: Int,
    val value: String,
    val label: String
)

@Immutable
data class LangGamifiedMetricItem(
    val id: String,
    val title: String,
    @DrawableRes val iconResId: Int,
    val category: LangGamifiedItemCategory
)

enum class LangGamifiedItemCategory {
    GRAMMAR,
    TRAVEL,
    VOCABULARY,
    // Add more categories as needed
}

// --------------------------------------------------------------------------------
// 2. UiState
// --------------------------------------------------------------------------------

@Immutable
data class LangDashboardUiState(
    val isLoading: Boolean = false,
    val userName: String = "Alex",
    val lessonQuote: String = "Home jcouse eebleer is saihban by niolot eeneed toch inlitlure.",
    val lessonProgress: Float = 0.75f, // 0.0f to 1.0f
    val lessonSteps: Int = 5,
    val currentStepIndex: Int = 2, // 0-indexed
    val currentModule: String = "German Basics",
    val currentLesson: String = "Lesson 3: Food & Drinks",
    val langMetrics: List<LangMetric> = emptyList(),
    val langGamifiedItems: List<LangGamifiedMetricItem> = emptyList(),
    val errorMessage: String? = null
)

// --------------------------------------------------------------------------------
// 3. UiEvents
// --------------------------------------------------------------------------------

sealed class LangDashboardUiEvent {
    data object OnProfileClick : LangDashboardUiEvent()
    data object OnContinueLearningClick : LangDashboardUiEvent()
    data class OnMetricClick(val metric: LangMetric) : LangDashboardUiEvent()
    data class OnGamifiedItemClick(val item: LangGamifiedMetricItem) : LangDashboardUiEvent()
    data object OnLessonProgressClick : LangDashboardUiEvent()
}