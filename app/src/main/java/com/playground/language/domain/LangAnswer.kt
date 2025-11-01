package com.playground.language.domain

data class LangAnswer(
    val id: String,
    val text: String,
    val foreignText: String?, // German text if relevant, null for English answers
    val isCorrect: Boolean = false
)