package com.playground.crypto.cleo.uistates

enum class CleoCollectionInterval(val label: String) {
    RECENT("Recent"),
    TRENDING("Trending"),
    POPULAR("Popular"),
    TOP("Top"),
    // ... add more as needed, e.g., ALL("All"), NEW("New")
    SHOP("Sh...")
}

data class CleoNftCollection(
    val id: String,
    val title: String,
    val creatorHandle: String,
    val imageUrl: String,
    val isFavorite: Boolean,
    val remainingSeconds: Long, // Use Long for timer countdown in seconds
    val currentPrice: String, // Formatted price string (e.g., "0.288 ETH")
)

data class CleoTopCollectionsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedInterval: CleoCollectionInterval = CleoCollectionInterval.RECENT,
    val availableIntervals: List<CleoCollectionInterval> = CleoCollectionInterval.entries,
    val recentCollection: CleoNftCollection? = null,
)

sealed class CleoTopCollectionsEvent {
    data class IntervalSelected(val interval: CleoCollectionInterval) : CleoTopCollectionsEvent()
    data object GridViewClicked : CleoTopCollectionsEvent()
    data object SeeAllClicked : CleoTopCollectionsEvent()
    data class FavoriteToggleClicked(val collectionId: String) : CleoTopCollectionsEvent()
    data class CollectionCardClicked(val collectionId: String) : CleoTopCollectionsEvent()
    data class QuickActionClicked(val collectionId: String) : CleoTopCollectionsEvent()
}