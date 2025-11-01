package com.playground.crypto.cleo.presentation.uistate

import com.playground.crypto.cleo.domain.model.CleoCollectionInterval
import com.playground.crypto.cleo.domain.model.CleoNftCollection

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
