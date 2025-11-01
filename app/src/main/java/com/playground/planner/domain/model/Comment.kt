package com.playground.planner.domain.model

data class Comment(
    val id: String,
    val author: UserMetadata,
    val dateLabel: String,
    val body: String
)
