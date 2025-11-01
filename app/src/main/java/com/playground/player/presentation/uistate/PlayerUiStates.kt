package com.playground.player.presentation.uistate

import androidx.compose.runtime.Immutable
import com.playground.player.domain.model.ContentTag
import com.playground.player.domain.model.WaveformMarker

@Immutable
data class LiveContentUiState(
    val title: String,
    val listenerCount: Int,
    val listenerAvatars: List<String>,
    val commentCount: Int,
    val currentTime: String,
    val contentTag: ContentTag,
    val waveformMarkers: List<WaveformMarker>,
    val isLiked: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
