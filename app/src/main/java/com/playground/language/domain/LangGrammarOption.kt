package com.playground.language.domain

data class LangGrammarOption(
    val id: String,
    val text: String,
    val completionPercentage: Int = 0, // e.g., 0% of users chose this
    val isCorrect: Boolean = false // Only known after checking
)