package com.playground.koh.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Category(
    val id: String,
    val title: String,
    val iconUrl: String,
    val isFavorite: Boolean = false
)
