package com.playground.crypto.presentation.visualizer

import androidx.compose.ui.geometry.Offset

data class VisualizerUiState(
    val isPlaying: Boolean,
    val isLoading: Boolean,
    val audioData: AudioData?,
    val playheadPosition: Float,
    val cuePoints: List<CuePoint>,
    val errorMessage: String?
)

data class AudioData(
    val waveform: List<Float>,
    val reactiveLines: List<List<Float>>
)

data class CuePoint(
    val id: Int,
    val timestamp: Long,
    val position: Offset
)