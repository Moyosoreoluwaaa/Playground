package com.playground.crypto.domain.model

data class Coin(
    val id: String, // e.g., "bitcoin"
    val symbol: String, // e.g., "BTC"
    val name: String, // e.g., "Bitcoin"
    val iconUrl: String,
    val currentPrice: Double,
    val priceChange24h: Double
)