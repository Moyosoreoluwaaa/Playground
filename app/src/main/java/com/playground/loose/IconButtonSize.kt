package com.playground.loose

import android.R.attr.contentDescription
import android.R.attr.tint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Locale

private val IconButtonSize = 52.dp
private val NextPrevIconButtonSize = 72.dp
private val IconInnerSize = 24.dp
private val NextPrevInnerSize = 36.dp

@Composable
fun NextPrevIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentDescription: String? = null,
    size: Dp = NextPrevIconButtonSize
) {
    Surface(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        color = backgroundColor,
        tonalElevation = 2.dp,
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(NextPrevInnerSize)
            )
        }
    }
}

@Composable
fun ActionIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentDescription: String? = null,
    size: Dp = IconButtonSize
) {
    Surface(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        color = backgroundColor,
        tonalElevation = 2.dp,
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(IconInnerSize)
            )
        }
    }
}

// IconButtonSize.kt - Update ActionRowAboveSeek for Video Player
@Composable
fun ActionRowAboveSeek(
    modifier: Modifier = Modifier,
    repeatMode: RepeatMode,
    onToggleRepeat: () -> Unit,
    onShowSpeed: () -> Unit,
    onRotate: () -> Unit,
    onToggleAudioOnly: () -> Unit,
    isAudioOnlyActive: Boolean,
    playbackSpeed: Float = 1f, // NEW parameter
    isLandscape: Boolean = false, // NEW parameter
    actionBgColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    activeColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Repeat
        ActionIconButton(
            onClick = onToggleRepeat,
            icon = when (repeatMode) {
                RepeatMode.OFF -> Icons.Filled.Repeat
                RepeatMode.ONE -> Icons.Filled.RepeatOne
                RepeatMode.ALL -> Icons.Filled.Repeat
            },
            backgroundColor = if (repeatMode == RepeatMode.OFF) actionBgColor else activeColor,
            contentDescription = "Repeat"
        )

        // Speed - Active color when not 1.0x
        ActionIconButton(
            onClick = onShowSpeed,
            icon = Icons.Filled.Speed,
            backgroundColor = if (playbackSpeed != 1f) activeColor else actionBgColor,
            contentDescription = "Speed"
        )

        // Rotation - Active color when landscape
        ActionIconButton(
            onClick = onRotate,
            icon = Icons.Filled.ScreenRotation,
            backgroundColor = if (isLandscape) activeColor else actionBgColor,
            contentDescription = "Rotate"
        )

        // Audio Only - Active color when enabled
        ActionIconButton(
            onClick = onToggleAudioOnly,
            icon = Icons.Filled.Headphones,
            backgroundColor = if (isAudioOnlyActive) activeColor else actionBgColor,
            contentDescription = "Audio only"
        )
    }
}

@Composable
fun CenterPlaybackControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    playButtonSize: Dp = 72.dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NextPrevIconButton(
            onClick = onPrevious,
            icon = Icons.Filled.SkipPrevious,
            contentDescription = "Previous",
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
        )

        FloatingActionButton(
            onClick = onPlayPause,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.size(playButtonSize)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(36.dp)
            )
        }

        NextPrevIconButton(
            onClick = onNext,
            icon = Icons.Filled.SkipNext,
            contentDescription = "Next",
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun PlayerSeekBar(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = formatDuration(currentPosition),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )

            Slider(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                onValueChange = { v ->
                    onSeek((v * duration).toLong())
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.onPrimary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        }
    }
}
