package com.playground.crypto.presentation.visualizer

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
private fun PlaybackControl(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .rotate(if (isPlaying) 0f else 90f)
            .clip(MaterialTheme.shapes.extraLarge),
        color = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Crossfade(targetState = isPlaying, label = "PlayPauseIcon") { isPlayingState ->
                Icon(
                    imageVector = if (isPlayingState) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlayingState) "Pause audio" else "Play audio",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(size / 2)
                )
            }
        }
    }
}

@Composable
private fun AudioVisualizer(
    audioData: AudioData,
    modifier: Modifier = Modifier
) {
    val histogramColor = MaterialTheme.colorScheme.surfaceVariant
    val line1Color = MaterialTheme.colorScheme.tertiary
    val line2Color = MaterialTheme.colorScheme.primary

    BoxWithConstraints(modifier = modifier) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val barWidth = width / audioData.waveform.size
        val halfBarWidth = barWidth / 2
        val strokeWidth = 2.toFloat()

        val linePath1 = remember(audioData.reactiveLines) {
            derivedStateOf {
                Path().apply {
                    if (audioData.reactiveLines.isNotEmpty() && audioData.reactiveLines[0].isNotEmpty()) {
                        moveTo(0f, height * (1 - audioData.reactiveLines[0][0]))
                        for (i in 1 until audioData.reactiveLines[0].size) {
                            lineTo(i * barWidth, height * (1 - audioData.reactiveLines[0][i]))
                        }
                    }
                }
            }
        }

        val linePath2 = remember(audioData.reactiveLines) {
            derivedStateOf {
                Path().apply {
                    if (audioData.reactiveLines.size > 1 && audioData.reactiveLines[1].isNotEmpty()) {
                        moveTo(0f, height * (1 - audioData.reactiveLines[1][0]))
                        for (i in 1 until audioData.reactiveLines[1].size) {
                            lineTo(i * barWidth, height * (1 - audioData.reactiveLines[1][i]))
                        }
                    }
                }
            }
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            audioData.waveform.forEachIndexed { index, value ->
                val barHeight = height * value
                drawRect(
                    brush = SolidColor(histogramColor),
                    topLeft = Offset(index * barWidth, height - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth * 0.8f, barHeight)
                )
            }
            drawPath(
                path = linePath1.value,
                color = line1Color,
                style = Stroke(width = strokeWidth)
            )
            drawPath(
                path = linePath2.value,
                color = line2Color,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}

@Composable
private fun PlayheadScrubber(
    position: Float,
    onPositionChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val playheadColor = MaterialTheme.colorScheme.onBackground
    val scrubberWidth = 4.dp
    val handleSize = 12.dp

    BoxWithConstraints(modifier = modifier) {
        val minX = 0f
        val maxX = constraints.maxWidth.toFloat()
        var currentPosition by remember { mutableStateOf(position * maxX) }

        val draggableState = rememberDraggableState(onDelta = { delta ->
            currentPosition = (currentPosition + delta).coerceIn(minX, maxX)
        })

        LaunchedEffect(currentPosition) {
            onPositionChanged(currentPosition / maxX)
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            val scrubberWidthPx = scrubberWidth.toPx()
            drawLine(
                color = playheadColor,
                start = Offset(currentPosition, 0f),
                end = Offset(currentPosition, size.height),
                strokeWidth = scrubberWidthPx,
                alpha = 0.8f
            )
        }

        Surface(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (currentPosition - handleSize.toPx() / 2).roundToInt(),
                        y = 0
                    )
                }
                .size(handleSize)
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal
                ),
            color = playheadColor
        ) {}
    }
}

@Composable
private fun AudioCuePoint(
    cuePoint: CuePoint,
    onTap: (CuePoint) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onTap(cuePoint) },
        modifier = modifier
            .size(36.dp)
            .clip(MaterialTheme.shapes.extraLarge),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = cuePoint.id.toString(),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}

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

                val density = LocalDensity.current
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
                    CuePoint(id = 2, timestamp = 15000, position = Offset(200f, 250f)),
                    CuePoint(id = 6, timestamp = 45000, position = Offset(500f, 150f))
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