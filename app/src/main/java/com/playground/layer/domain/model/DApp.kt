package com.playground.layer.domain.model

data class DApp(
    val id: String,
    val name: String,
    val category: String, // e.g., DeFi, Marketplace
    val iconUrl: String,
    val badgeText: String? = null
)