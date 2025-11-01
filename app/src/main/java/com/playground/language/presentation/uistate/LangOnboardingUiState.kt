package com.playground.language.presentation.uistate

import androidx.compose.runtime.Immutable
import com.playground.language.LangDailyGoal
import com.playground.language.LangLanguageOption

@Immutable
data class LangOnboardingUiState(
    val selectedGoal: LangDailyGoal = LangDailyGoal.ThirtyMin,
    val availableLanguages: List<LangLanguageOption> = emptyList(),
    val selectedLanguageCode: String? = null,
    val startLearningEnabled: Boolean = false,
    val headerText: String = "Welcome to Lingo!",
    val instructionText: String = "Set your daily goal to get started",
)

sealed class LangOnboardingUiEvent {
    data object OnBackClick : LangOnboardingUiEvent()
    data object OnSkipClick : LangOnboardingUiEvent()
    data object OnStartLearningClick : LangOnboardingUiEvent()
    data class OnGoalSelected(val goal: LangDailyGoal) : LangOnboardingUiEvent()
    data class OnLanguageSelected(val language: LangLanguageOption) : LangOnboardingUiEvent()
}