package com.playground.crypto.presentation.player

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color

// Mock data for different states
val mockContentState = LiveContentUiState(
    title = "Rays of the Earth",
    listenerCount = 342,
    listenerAvatars = listOf(
        "https://randomuser.me/api/portraits/men/1.jpg",
        "https://randomuser.me/api/portraits/women/2.jpg",
        "https://randomuser.me/api/portraits/men/3.jpg"
    ),
    commentCount = 212,
    currentTime = "03:43",
    contentTag = ContentTag.POETRY,
    waveformMarkers = listOf(
        WaveformMarker(id = 2, position = 0.3f, label = "2", color = Color.Magenta),
        WaveformMarker(id = 6, position = 0.65f, label = "6", color = Color.Yellow),
        WaveformMarker(id = 7, position = 0.75f, label = "7", color = Color.Green)
    )
)

val mockLoadingState = mockContentState.copy(isLoading = true)
val mockErrorState = mockContentState.copy(error = "Failed to load content.", isLoading = false)

@Preview(name = "Content State")
@Composable
fun LiveContentScreenContentPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        LiveContentScreen(
            uiState = mockContentState,
            onLikeClicked = {},
            onCommentClicked = {},
            onMarkerClicked = {}
        )
    }
}

@Preview(name = "Loading State")
@Composable
fun LiveContentScreenLoadingPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        LiveContentScreen(
            uiState = mockLoadingState,
            onLikeClicked = {},
            onCommentClicked = {},
            onMarkerClicked = {}
        )
    }
}

@Preview(name = "Error State")
@Composable
fun LiveContentScreenErrorPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        LiveContentScreen(
            uiState = mockErrorState,
            onLikeClicked = {},
            onCommentClicked = {},
            onMarkerClicked = {}
        )
    }
}