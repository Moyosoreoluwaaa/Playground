package com.playground.player.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.player.presentation.components.LiveContentCard
import com.playground.player.presentation.uistate.LiveContentUiState
import com.playground.player.data.source.mockContentState
import com.playground.player.data.source.mockErrorState
import com.playground.player.data.source.mockLoadingState
import androidx.compose.material3.darkColorScheme

@Composable
fun LiveContentScreen(
    uiState: LiveContentUiState,
    onLikeClicked: () -> Unit,
    onCommentClicked: () -> Unit,
    onMarkerClicked: (markerId: Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${uiState.error}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LiveContentCard(
                title = uiState.title,
                listenerCount = uiState.listenerCount,
                avatars = uiState.listenerAvatars,
                waveformMarkers = uiState.waveformMarkers,
                currentTime = uiState.currentTime,
                contentTag = uiState.contentTag.displayName,
                commentCount = uiState.commentCount,
                onLikeClicked = onLikeClicked,
                onCommentClicked = onCommentClicked,
                onMarkerClicked = onMarkerClicked
            )
        }
    }
}

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
