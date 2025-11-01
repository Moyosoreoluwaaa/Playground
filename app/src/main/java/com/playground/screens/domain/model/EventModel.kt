package com.playground.screens.domain.model

data class EventModel(
    val id: String,
    val name: String,
    val date: String,
    val imageUrl: String,
    val isFavorited: Boolean
)
