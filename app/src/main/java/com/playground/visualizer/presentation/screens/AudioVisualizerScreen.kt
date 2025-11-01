package com.playground.visualizer.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.playground.visualizer.domain.model.AudioData
import com.playground.visualizer.domain.model.CuePoint
import com.playground.visualizer.presentation.components.AudioCuePoint
import com.playground.visualizer.presentation.components.AudioVisualizer
import com.playground.visualizer.presentation.components.PlaybackControl
import com.playground.visualizer.presentation.components.PlayheadScrubber
import com.playground.visualizer.presentation.uistate.VisualizerUiState
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun AudioVisualizerScreen(
    uiState: VisualizerUiState,
    onPlayheadPositionChanged: (Float) -> Unit,
    onCuePointTapped: (CuePoint) -> Unit,
    onPlaybackControlClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.errorMessage != null) {
            Text(
                text = "Error: ${uiState.errorMessage}",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.error
            )
        } else if (uiState.audioData != null) {
            // The main visualizer area
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.5f)
                    .align(Alignment.Center)
            ) {
                AudioVisualizer(
                    audioData = uiState.audioData,
                    modifier = Modifier.matchParentSize()
                )

                uiState.cuePoints.forEach { cuePoint ->
                    Box(modifier = Modifier.offset {
                        IntOffset(
                            x = cuePoint.position.x.roundToInt(),
                            y = cuePoint.position.y.roundToInt()
                        )
                    }) {
                        AudioCuePoint(
                            cuePoint = cuePoint,
                            onTap = onCuePointTapped
                        )
                    }
                }

                // Playhead Scrubber is laid out on top of the visualizer
                PlayheadScrubber(
                    position = uiState.playheadPosition,
                    onPositionChanged = onPlayheadPositionChanged,
                    modifier = Modifier.matchParentSize()
                )
            }

            // The main playback control button
            PlaybackControl(
                isPlaying = uiState.isPlaying,
                onClick = onPlaybackControlClicked,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AudioVisualizerScreenPreview() {
    val sampleData = remember {
        val random = Random(0)
        AudioData(
            waveform = List(50) { random.nextFloat() * 0.8f + 0.1f },
            reactiveLines = listOf(
                List(50) { random.nextFloat() * 0.5f + 0.5f },
                List(50) { random.nextFloat() * 0.5f }
            )
        )
    }

    val uiState = remember {
        mutableStateOf(
            VisualizerUiState(
                isPlaying = true,
                isLoading = false,
                audioData = sampleData,
                playheadPosition = 0.3f,
                cuePoints = listOf(
                    CuePoint(id = 2, timestamp = 15000, position = androidx.compose.ui.geometry.Offset(200f, 250f)),
                    CuePoint(id = 6, timestamp = 45000, position = androidx.compose.ui.geometry.Offset(500f, 150f))
                ),
                errorMessage = null
            )
        )
    }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AudioVisualizerScreen(
                uiState = uiState.value,
                onPlayheadPositionChanged = { newPosition ->
                    uiState.value = uiState.value.copy(playheadPosition = newPosition)
                },
                onCuePointTapped = { cuePoint ->
                    // Logic to jump to cue point
                },
                onPlaybackControlClicked = {
                    uiState.value = uiState.value.copy(isPlaying = !uiState.value.isPlaying)
                }
            )
        }
    }
}
