package com.playground.language.presentation.uistate

import com.playground.language.data.LangBadge
import com.playground.language.data.LangStreakDay

data class LangAchievementsUiState(
    val isLoading: Boolean = false,
    val studyGoalMinutes: Int = 30,
    val currentStreakDays: Int = 7,
    val streakCalendar: List<LangStreakDay> = emptyList(), // Daily log for the calendar visual
    val earnedBadges: List<LangBadge> = emptyList(),
    val streakMotivationText: String = "You're on fire! Keep up great work!",
    val errorMessage: String? = null
)

sealed class LangAchievementsUiEvent {
    data object OnBackClick : LangAchievementsUiEvent()
    data object OnContinueLearningClick : LangAchievementsUiEvent()
    data class OnBadgeClick(val badge: LangBadge) : LangAchievementsUiEvent()
}