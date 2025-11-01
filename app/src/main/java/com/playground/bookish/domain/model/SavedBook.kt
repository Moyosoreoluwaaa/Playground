package com.playground.bookish.domain.model

data class SavedBook(
    val id: String,
    val title: String,
    val coverImageUri: String,
    val isTopBook: Boolean,
    val isSaved: Boolean,
    val authorName: String,
    val authorImageUri: String
)
