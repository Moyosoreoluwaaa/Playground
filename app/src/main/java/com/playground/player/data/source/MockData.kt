package com.playground.player.data.source

import androidx.compose.ui.graphics.Color
import com.playground.player.domain.model.ContentTag
import com.playground.player.domain.model.WaveformMarker
import com.playground.player.presentation.uistate.LiveContentUiState

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
