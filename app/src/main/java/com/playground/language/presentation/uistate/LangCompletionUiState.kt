package com.playground.language.presentation.uistate

import com.playground.language.data.LangAchievementMetric

data class LangCompletionUiState(
    val isLoading: Boolean = false,
    val xpGained: Int = 50,
    val isNewStreak: Boolean = true,
    val streakValue: Int = 5,
    val timeSpentMinutes: Int = 6,
    val newBadgeName: String? = "Word Master", // Nullable if no new badge earned
    val achievementMetrics: List<LangAchievementMetric> = emptyList(),
    val errorMessage: String? = null
)

sealed class LangCompletionUiEvent {
    data object OnContinueLearningClick : LangCompletionUiEvent()
    // No other interactions apparent on this static screen.
}