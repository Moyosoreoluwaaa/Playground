package com.playground.language.presentation.uistate

import androidx.compose.runtime.Immutable

@Immutable
data class LangMatchWord(val id: String, val text: String, val type: LangMatchWordType)
enum class LangMatchWordType { FOREIGN, TRANSLATION }

@Immutable
data class LangMatchPair(val wordIdA: String, val wordIdB: String)

@Immutable
data class LangVocabularyMatchPage(
    val foreignWords: List<LangMatchWord>,
    val translationWords: List<LangMatchWord>,
    val correctMatches: List<LangMatchPair>, // The solution for the page
)












