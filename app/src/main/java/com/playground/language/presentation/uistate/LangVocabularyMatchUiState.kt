package com.playground.language.presentation.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

@Immutable
data class LangVocabularyMatchUiState(
    val lessonTitle: String = "Vocabulary Match",
    val pages: List<LangVocabularyMatchPage> = emptyList(),
    val currentMatches: List<LangMatchPair> = emptyList(),
    val currentPage: Int = 0,
    val pageCount: Int = 1,
    val liveStreak: Int = 8,
    val livePoints: Int = 120,
    val liveTime: String = "0:45",
    val checkMatchesEnabled: Boolean = false,
) {
    val currentPageData: LangVocabularyMatchPage?
        get() = pages.getOrNull(currentPage)
}

sealed class LangVocabularyMatchUiEvent {
    data object OnBackClick : LangVocabularyMatchUiEvent()
    data object OnCheckMatchesClick : LangVocabularyMatchUiEvent()
    data class OnPageChange(val newPageIndex: Int) : LangVocabularyMatchUiEvent()
    data class OnMatchMade(val newMatches: List<LangMatchPair>) : LangVocabularyMatchUiEvent()
}

  data class DragState(
      val startWord: LangMatchWord,
      val startOffset: Offset,
      val currentOffset: Offset
)
