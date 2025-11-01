package com.playground.crypto.presentation.bookish

import androidx.compose.runtime.Immutable

@Immutable
data class SavedBooksUiState(
    val savedBooks: List<SavedBook> = emptyList(),
    val filters: List<FilterState>,
    val featuredBook: FeaturedBook,
    val selectedFilter: TabType = TabType.HITS,
    val isLoading: Boolean = false,
    val error: String? = null
)

@Immutable
data class FilterState(
    val type: TabType,
    val text: String
)

@Immutable
data class SavedBook(
    val id: String,
    val title: String,
    val coverImageUri: String,
    val isTopBook: Boolean,
    val isSaved: Boolean,
    val authorName: String,
    val authorImageUri: String
)

@Immutable
data class FeaturedBook(
    val id: String,
    val title: String,
    val description: String,
    val coverImageUri: String,
    val authorName: String,
    val listenProgress: Float
)

enum class TabType {
    HITS, POLL
}