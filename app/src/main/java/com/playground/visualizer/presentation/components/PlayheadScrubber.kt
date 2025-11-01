package com.playground.visualizer.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun PlayheadScrubber(
    position: Float,
    onPositionChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    scrubberWidth: Dp = 4.dp,
    handleSize: Dp = 12.dp
) {
    val playheadColor = MaterialTheme.colorScheme.onBackground

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
                start = androidx.compose.ui.geometry.Offset(currentPosition, 0f),
                end = androidx.compose.ui.geometry.Offset(currentPosition, size.height),
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
