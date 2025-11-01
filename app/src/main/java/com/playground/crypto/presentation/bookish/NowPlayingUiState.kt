package com.playground.crypto.presentation.bookish

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

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