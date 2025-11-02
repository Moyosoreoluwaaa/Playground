package com.playground.loose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage

@Composable
fun MiniPlayer(
    currentAudio: AudioItem?,
    currentVideo: VideoItem?,
    isPlaying: Boolean,
    isAudioMode: Boolean,
    onPlayPause: () -> Unit,
    onShowQueue: () -> Unit, // New parameter for the queue button
    onNext: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = if (isAudioMode) currentAudio?.title else currentVideo?.title
    val subtitle = if (isAudioMode) currentAudio?.artist else "Video"
    val artwork = if (isAudioMode) currentAudio?.albumArtUri else currentVideo?.thumbnailUri

    if (title != null) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .height(72.dp)
                .clickable(onClick = onClick),
            shadowElevation = 8.dp,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Artwork
                AsyncImage(
                    model = artwork,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Title and subtitle
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPlayPause) {
                        Icon(
                            imageVector = if (isPlaying) {
                                Icons.Filled.Pause
                            } else {
                                Icons.Filled.PlayArrow
                            },
                            contentDescription = if (isPlaying) "Pause" else "Play"
                        )
                    }

                    IconButton(onClick = onNext) {
                        Icon(
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = "Next"
                        )
                    }

                    // New Queue button
                    IconButton(onClick = onShowQueue) {
                        Icon(
                            imageVector = Icons.Filled.QueueMusic,
                            contentDescription = "Show Queue"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiniPlayerSpacer(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Spacer(modifier = modifier.height(72.dp))
    }
}

// Dummy data classes if they don't exist yet or to make previews self-contained.
// If you already have these defined, you can remove these dummy ones.
//data class AudioItem(val title: String, val artist: String, val albumArtUri: String)

// --- PREVIEWS ---

@Preview(name = "Playing Audio", showBackground = true)
@Composable
fun MiniPlayerAudioPlayingPreview() {
    val sampleAudio = AudioItem(
        title = "The Golden Hour",
        artist = "The Midnight Bloom",
        albumArtUri = "https://picsum.photos/seed/a/200".toUri(), // Placeholder image
        duration = 180000L,
        size = 100000L,
        dateAdded = 0L,
        path = "",
        id = 0L,
        uri = "".toUri(),
        album = null
    )
    MaterialTheme {
        MiniPlayer(
            currentAudio = sampleAudio,
            currentVideo = null,
            isPlaying = true,
            isAudioMode = true,
            onPlayPause = {},
            onShowQueue = {},
            onNext = {},
            onClick = {}
        )
    }
}

@Preview(name = "Paused Video", showBackground = true)
@Composable
fun MiniPlayerVideoPausedPreview() {
    val sampleVideo = VideoItem(
        title = "A very long video title that should be truncated properly",
        thumbnailUri = "https://picsum.photos/seed/v/200".toUri(), // Placeholder image
        duration = 180000L,
        size = 100000L,
        dateAdded = 0L,
        path = "",
        id = 0L,
        uri = "".toUri(),
        width = 0,
        height = 0
    )
    MaterialTheme {
        MiniPlayer(
            currentAudio = null,
            currentVideo = sampleVideo,
            isPlaying = false,
            isAudioMode = false,
            onPlayPause = {},
            onShowQueue = {},
            onNext = {},
            onClick = {}
        )
    }
}

@Preview(name = "Hidden State", showBackground = true)
@Composable
fun MiniPlayerHiddenPreview() {
    // The MiniPlayer itself will not render because title is null
    MaterialTheme {
        Box(modifier = Modifier.fillMaxWidth().height(72.dp)) {
            MiniPlayer(
                currentAudio = null,
                currentVideo = null,
                isPlaying = false,
                isAudioMode = true,
                onPlayPause = {},
                onShowQueue = {},
                onNext = {},
                onClick = {}
            )
            // This preview demonstrates that nothing is shown when there's no media.
            Text("MiniPlayer is hidden when no media is provided", modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Preview(name = "MiniPlayer Spacer", showBackground = true)
@Composable
fun MiniPlayerSpacerPreview(
    @PreviewParameter(BooleanPreviewProvider::class) isVisible: Boolean
) {
    // Shows the spacer when visible and nothing when not.
    MaterialTheme {
        Column {
            Text(text = "Content above the player")
            MiniPlayerSpacer(isVisible = isVisible)
            Text(text = "Content below the player (will be pushed down if spacer is visible)")
        }
    }
}

// Helper class to provide both true and false for previews
class BooleanPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}
