package com.playground.crypto.presentation.bookish// In a new file, e.g., screens/SavedBooksScreen.kt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
// SavedBooksScreen.kt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState

@Composable
fun SavedBooksScreen(
    uiState: SavedBooksUiState,
    onBackClicked: () -> Unit,
    onViewModeToggled: () -> Unit,
    onFilterSelected: (TabType) -> Unit,
    onBookCardClicked: (bookId: String) -> Unit,
    onSaveToggled: (bookId: String) -> Unit,
    onUnlockClicked: () -> Unit,
    onListenPreviewClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Top Bar
        SavedTopBar(
            title = "Saved Books",
            onBackClicked = onBackClicked,
            onRightIconClicked = onViewModeToggled,
            rightIcon = Icons.Default.ViewList
        )

        // Filters/Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(uiState.filters) { filter ->
                PillFilterChip(
                    text = filter.text,
                    isSelected = filter.type == uiState.selectedFilter,
                    onClick = { onFilterSelected(filter.type) }
                )
            }
        }

        // Vertical Parallax List
        VerticalParallaxLazyColumn(
            modifier = Modifier.weight(1f),
            count = uiState.savedBooks.size,
            listState = listState,
            contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp)
        ) { index ->
            val book = uiState.savedBooks[index]
            SavedBookCard(
                book = book,
                onCardClicked = onBookCardClicked,
                onSaveToggled = onSaveToggled
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Featured Card at the bottom
        FeaturedBookCard(
            featuredBook = uiState.featuredBook,
            onUnlockClicked = onUnlockClicked,
            onListenPreviewClicked = onListenPreviewClicked,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SavedBooksScreenFullPreview() {
    MaterialTheme {
        val mockSavedBooks = listOf(
            SavedBook(
                id = "1",
                title = "Rays of the Earth",
                coverImageUri = "https://example.com/cover1.jpg",
                isTopBook = true,
                isSaved = true,
                authorName = "Lina Kostenko",
                authorImageUri = "https://example.com/author1.jpg"
            ),
            SavedBook(
                id = "2",
                title = "Math Secrets",
                coverImageUri = "https://example.com/cover2.jpg",
                isTopBook = true,
                isSaved = true,
                authorName = "Author Two",
                authorImageUri = "https://example.com/author2.jpg"
            ),
            SavedBook(
                id = "3",
                title = "The Art of Writing",
                coverImageUri = "https://example.com/cover3.jpg",
                isTopBook = false,
                isSaved = false,
                authorName = "Author Three",
                authorImageUri = "https://example.com/author3.jpg"
            ),
            SavedBook(
                id = "4",
                title = "A Guide to Gardening",
                coverImageUri = "https://example.com/cover4.jpg",
                isTopBook = false,
                isSaved = true,
                authorName = "Author Four",
                authorImageUri = "https://example.com/author4.jpg"
            ),
        )
        val mockFeaturedBook = FeaturedBook(
            id = "5",
            title = "A minimalist and uplifting book cover...",
            description = "The central focus is a golden compass with intricate details, glowing softly.",
            coverImageUri = "https://example.com/featured_cover.jpg",
            authorName = "Rays of the Earth",
            listenProgress = 0.8f
        )
        val mockUiState = SavedBooksUiState(
            savedBooks = mockSavedBooks,
            filters = listOf(
                FilterState(TabType.HITS, "Today's Hits"),
                FilterState(TabType.POLL, "Poll")
            ),
            featuredBook = mockFeaturedBook
        )

        Surface(modifier = Modifier.fillMaxSize()) {
            SavedBooksScreen(
                uiState = mockUiState,
                onBackClicked = { /* Handle back */ },
                onViewModeToggled = { /* Handle view toggle */ },
                onFilterSelected = { /* Handle filter */ },
                onBookCardClicked = { /* Handle card click */ },
                onSaveToggled = { /* Handle save toggle */ },
                onUnlockClicked = { /* Handle unlock */ },
                onListenPreviewClicked = { /* Handle preview click */ }
            )
        }
    }
}