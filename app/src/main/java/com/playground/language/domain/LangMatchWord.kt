package com.playground.language.domain

import androidx.compose.runtime.Immutable

// Domain Models (New for this screen)
@Immutable
data class LangMatchWord(val id: String, val text: String, val type: LangMatchWordType)
enum class LangMatchWordType { FOREIGN, TRANSLATION }
@Immutable data class LangMatchPair(val wordIdA: String, val wordIdB: String) // Represents a user-made connection

// UI State (New Screen)
@Immutable
data class LangVocabularyMatchUiState(
    val isLoading: Boolean = false,
    val lessonTitle: String = "Vocabulary Match",
    val instruction: String = "Drag & drop the words to match",
    val foreignWords: List<LangMatchWord> = emptyList(),
    val translationWords: List<LangMatchWord> = emptyList(),
    val currentMatches: List<LangMatchPair> = emptyList(), // User's active connections
    val liveStreak: Int = 8,
    val livePoints: Int = 120,
    val liveTime: String = "0:45",
    val checkMatchesEnabled: Boolean = false,
    val isChecked: Boolean = false, // Post-submission state
    val errorMessage: String? = null
)

// UI Events (New Screen)
sealed class LangVocabularyMatchUiEvent {
    data object OnBackClick : LangVocabularyMatchUiEvent()
    data object OnCheckMatchesClick : LangVocabularyMatchUiEvent()
    data class OnWordTapped(val wordId: String) : LangVocabularyMatchUiEvent()
    // A drag-and-drop interaction often simplifies to:
    data class OnAttemptMatch(val wordIdA: String, val wordIdB: String) : LangVocabularyMatchUiEvent()
}