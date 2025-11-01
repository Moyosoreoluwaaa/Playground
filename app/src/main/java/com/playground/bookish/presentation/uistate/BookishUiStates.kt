package com.playground.bookish.presentation.uistate

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Immutable
import com.playground.bookish.domain.model.FeaturedBook
import com.playground.bookish.domain.model.Plan
import com.playground.bookish.domain.model.SavedBook

@Immutable
data class BookCardState(
    val id: String,
    val title: String,
    val coverImageUri: String,
    val isPlaying: Boolean,
    val currentTime: String,
    val progress: Float
)

@Immutable
data class NowPlayingUiState(
    val bookTitle: String,
    val author: String,
    val bookImageUri: String,
    val isLiked: Boolean,
    val isPlaying: Boolean,
    val listeners: List<String>, // List of listener profile image URIs
    val currentPosition: String,
    val totalTime: String,
    val audioWaveformData: List<Float>,
    val chartData: List<Pair<Float, String>>,
    val playbackProgress: Float
)

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

enum class TabType {
    HITS, POLL
}

data class ListenAndLearnUiState(
    val userName: String = "Emily",
    val profileImageUri: String,
    val myBooks: List<BookCardState> = emptyList(),
    val plan: Plan = Plan("Yearly plan", "15 books", 0.46f),
    val pagerState: PagerState
)