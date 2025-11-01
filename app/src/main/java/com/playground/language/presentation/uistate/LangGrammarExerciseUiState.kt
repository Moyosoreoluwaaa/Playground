package com.playground.language.presentation.uistate

import com.playground.language.domain.LangGrammarOption

data class LangGrammarExerciseUiState(
    val isLoading: Boolean = false,
    val lessonTitle: String = "Grammar Exercise",
    val questionSentence: String = "Die Katze ____ auf dem Stuhll.",
    val instruction: String = "Fill in the correct verb congglation", // sic
    val options: List<LangGrammarOption> = emptyList(),
    val selectedOptionId: String? = null,
    val isAnswerChecked: Boolean = false,
    val checkButtonEnabled: Boolean = false,
    val errorMessage: String? = null
)

sealed class LangGrammarExerciseUiEvent {
    data class OnOptionSelected(val option: LangGrammarOption) : LangGrammarExerciseUiEvent()
    data object OnCheckAnswer : LangGrammarExerciseUiEvent()
    // Data object OnBackClick is implicit but often necessary
}