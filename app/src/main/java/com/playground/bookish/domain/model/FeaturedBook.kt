package com.playground.bookish.domain.model

data class FeaturedBook(
    val id: String,
    val title: String,
    val description: String,
    val coverImageUri: String,
    val authorName: String,
    val listenProgress: Float
)
