package com.playground.visualizer.presentation.uistate

import androidx.compose.ui.geometry.Offset
import com.playground.visualizer.domain.model.AudioData
import com.playground.visualizer.domain.model.CuePoint

data class VisualizerUiState(
    val isPlaying: Boolean,
    val isLoading: Boolean,
    val audioData: AudioData?,
    val playheadPosition: Float,
    val cuePoints: List<CuePoint>,
    val errorMessage: String?
)
