package com.playground.visualizer.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PlaybackControl(
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
