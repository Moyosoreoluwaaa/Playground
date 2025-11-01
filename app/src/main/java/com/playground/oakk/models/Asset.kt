package com.playground.oakk.models

// Represents a single investment asset
data class Asset(
    val id: String,
    val ticker: String, // e.g., "AMZN"
    val name: String, // e.g., "Bank of America"
    val description: String, // e.g., "(86 shares)"
    val logoUrl: String,
    val valueInSar: String, // Pre-formatted
    val percentageChange: Double
)

// Represents a category of assets, e.g., "Stocks"
data class AssetCategory(
    val title: String,
    val assets: List<Asset>
)