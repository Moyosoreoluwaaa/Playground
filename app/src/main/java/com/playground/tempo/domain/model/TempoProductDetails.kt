package com.playground.tempo.domain.model

data class TempoProductDetails(
    val id: String,
    val name: String,
    val price: String,
    val description: String,
    val imageUrls: List<String>,
    val isFavorited: Boolean
)
