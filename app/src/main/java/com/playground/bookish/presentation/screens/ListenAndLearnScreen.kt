package com.playground.bookish.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.bookish.presentation.components.BookCard
import com.playground.bookish.presentation.components.PlanProgressCard
import com.playground.bookish.presentation.components.SearchInput
import com.playground.bookish.presentation.components.TopBar
import com.playground.bookish.presentation.uistate.BookCardState
import com.playground.bookish.presentation.uistate.ListenAndLearnUiState
import kotlinx.coroutines.launch

@Composable
fun ListenAndLearnScreen(
    uiState: ListenAndLearnUiState,
    onSearchClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onNotificationsClicked: () -> Unit,
    onBookCardClicked: (bookId: String) -> Unit,
    onBookPlayPauseClicked: (bookId: String) -> Unit,
    onPlanCardClicked: () -> Unit,
    onNextBookClicked: () -> Unit,
    onPreviousBookClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().statusBarsPadding()
    ) {
        // Top Bar
        TopBar(
            profileImageUri = uiState.profileImageUri,
            userName = uiState.userName,
            onProfileClicked = onProfileClicked,
            onNotificationsClicked = onNotificationsClicked
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        SearchInput(
            onSearchClicked = onSearchClicked,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // My Books Section Heading & Navigation
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Books",
                style = MaterialTheme.typography.titleLarge
            )
            Row {
                IconButton(onClick = onPreviousBookClicked) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous book"
                    )
                }
                IconButton(onClick = onNextBookClicked) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next book"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // My Books Horizontal Pager
        // Using HorizontalPager for swipeable content as per the spec
        // Corrected My Books Horizontal Pager
        HorizontalPager(
            state = uiState.pagerState,
            // Provide an explicit height to the Pager. You can also use a fixed value.
            modifier = Modifier.fillMaxWidth().height(260.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { page ->
            // The pager holds one book card at a time, so we just display the item at the current page index
            val book = uiState.myBooks[page]
            BookCard(
                state = book,
                onPlayPauseClicked = onBookPlayPauseClicked,
                onBookCardClicked = onBookCardClicked,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Plan Progress Card
        PlanProgressCard(
            plan = uiState.plan,
            onCardClicked = onPlanCardClicked,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListenAndLearnScreenPreview() {
    MaterialTheme {
        val coroutineScope = rememberCoroutineScope()
        val mockBooks = listOf(
            BookCardState(
                id = "1",
                title = "Unlock Your Inner God",
                coverImageUri = "https://example.com/book1.jpg",
                isPlaying = false,
                currentTime = "02:34:43",
                progress = 0.5f
            ),
            BookCardState(
                id = "2",
                title = "The Magic Forest",
                coverImageUri = "https://example.com/book2.jpg",
                isPlaying = true,
                currentTime = "02:34:43",
                progress = 0.8f
            ),
            BookCardState(
                id = "3",
                title = "Rays of the Earth",
                coverImageUri = "https://example.com/book3.jpg",
                isPlaying = false,
                currentTime = "00:15:00",
                progress = 0.05f
            )
        )

        val pagerState = rememberPagerState(pageCount = { mockBooks.size })

         val mockUiState = ListenAndLearnUiState(
             userName = "Emily",
             profileImageUri = "https://example.com/profile.jpg",
             myBooks = mockBooks,
             pagerState = pagerState
         )

         val onNextBookClicked: () -> Unit = {
             coroutineScope.launch {
                 val currentPage = pagerState.currentPage
                 val nextPage = (currentPage + 1) % pagerState.pageCount
                 pagerState.animateScrollToPage(nextPage)
             }
         }
         val onPreviousBookClicked: () -> Unit = {
             coroutineScope.launch {
                 val currentPage = pagerState.currentPage
                 val prevPage = if (currentPage - 1 < 0) pagerState.pageCount - 1 else currentPage - 1
                 pagerState.animateScrollToPage(prevPage)
             }
         }

         ListenAndLearnScreen(
             uiState = mockUiState,
             onSearchClicked = { /* Handle search */ },
             onProfileClicked = { /* Handle profile click */ },
             onNotificationsClicked = { /* Handle notifications click */ },
             onBookCardClicked = { bookId -> /* Handle book card click */ },
             onBookPlayPauseClicked = { bookId -> /* Handle play/pause click */ },
             onPlanCardClicked = { /* Handle plan card click */ },
             onNextBookClicked = onNextBookClicked,
             onPreviousBookClicked = onPreviousBookClicked
         )
    }
}
