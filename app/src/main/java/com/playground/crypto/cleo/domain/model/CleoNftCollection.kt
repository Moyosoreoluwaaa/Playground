package com.playground.crypto.cleo.domain.model

data class CleoNftCollection(
    val id: String,
    val title: String,
    val creatorHandle: String,
    val imageUrl: String,
    val isFavorite: Boolean,
    val remainingSeconds: Long, // Use Long for timer countdown in seconds
    val currentPrice: String, // Formatted price string (e.g., "0.288 ETH")
)
