package com.playground.language.presentation.uistate

import androidx.compose.runtime.Immutable

// Domain Models (New for this screen)
@Immutable
data class LangInterest(val id: String, val name: String, val isSelected: Boolean = false)

// UI State (New Screen)
@Immutable
data class LangInterestSelectionUiState(
    val isLoading: Boolean = false,
    val headerText: String = "Choose Your Interests",
    val instructionText: String = "Select topics to personalize learning plan",
    val availableInterests: List<LangInterest> = emptyList(),
    val selectedInterestsCount: Int = 0,
    val startLearningEnabled: Boolean = false,
    val errorMessage: String? = null
)

// UI Events (New Screen)
sealed class LangInterestSelectionUiEvent {
    data object OnBackClick : LangInterestSelectionUiEvent()
    data object OnSkipClick : LangInterestSelectionUiEvent()
    data object OnStartLearningClick : LangInterestSelectionUiEvent()
    data class OnInterestToggled(val interest: LangInterest) : LangInterestSelectionUiEvent()
}