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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Context-aware MiniPlayer that displays the correct media based on PlaybackContext
 * This is the SINGLE source of truth for the mini player UI
 */
@Composable
fun MiniPlayer(
    currentContext: PlaybackContext,
    currentAudio: AudioItem?,
    currentVideo: VideoItem?,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onShowQueue: () -> Unit,
    onNext: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine what to show based on context
    val (title, subtitle, artwork) = when (currentContext) {
        PlaybackContext.NONE -> return // Don't show mini player if nothing is playing

        PlaybackContext.AUDIO -> {
            if (currentAudio == null) return
            Triple(
                currentAudio.title,
                currentAudio.artist ?: "Unknown Artist",
                currentAudio.albumArtUri
            )
        }

        PlaybackContext.VIDEO_VISUAL -> {
            if (currentVideo == null) return
            Triple(
                currentVideo.title,
                "Video",
                currentVideo.thumbnailUri
            )
        }

        PlaybackContext.VIDEO_AUDIO_ONLY -> {
            if (currentVideo == null) return
            Triple(
                currentVideo.title,
                "Video (Audio Only)", // Clear indicator of mode
                currentVideo.thumbnailUri
            )
        }
    }

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
                    overflow = TextOverflow.Ellipsis,
                    color = if (currentContext == PlaybackContext.VIDEO_AUDIO_ONLY) {
                        MaterialTheme.colorScheme.primary // Highlight video-as-audio
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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

@Composable
fun MiniPlayerSpacer(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Spacer(modifier = modifier.height(72.dp))
    }
}