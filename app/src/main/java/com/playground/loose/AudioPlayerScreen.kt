package com.playground.loose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AudioPlayerScreen(
    currentAudio: AudioItem?,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    repeatMode: RepeatMode,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onSkipForward: () -> Unit,
    onSkipBackward: () -> Unit,
    onToggleRepeat: () -> Unit,
    onBack: () -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit
) {
    val context = LocalContext.current

    var bgColors by remember { mutableStateOf(listOf(Color(0xFF121212), Color(0xFF000000))) }
    var showSpeedSheet by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1f) }

    // Extract palette in LaunchedEffect (use extractPalette util if available)
    LaunchedEffect(currentAudio?.albumArtUri) {
        currentAudio?.albumArtUri?.let { uri ->
            // extractPalette should return androidx.palette.graphics.Palette or null
            val p = try {
                extractPalette(context, uri.toString())
            } catch (t: Throwable) {
                null
            }
            val swatch = p?.dominantSwatch
            val primary = swatch?.rgb?.let { Color(it) } ?: Color(0xFF121212)
            val secondary = primary.copy(alpha = 0.88f)
            bgColors = listOf(primary, secondary)
        }
    }

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(bgColors))
                .padding(paddingValues)
                .padding(20.dp)
//                .padding(vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionIconButton(
                    onClick = onBack,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentDescription = "Back",

                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                // Album Art centered top
                AlbumArtwork(
                    albumArtUri = currentAudio?.albumArtUri,
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Title / Artist / Album below artwork (moved here per request)
                Text(
                    text = currentAudio?.title ?: "No track",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                currentAudio?.artist?.let {
                    Text(
                        text = it,
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                currentAudio?.album?.let {
                    Text(
                        text = it,
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Action row above seek (repeat, speed, headphones) - no rotate
                ActionRowAboveSeek(
                    repeatMode = repeatMode,
                    onToggleRepeat = onToggleRepeat,
                    onShowSpeed = { showSpeedSheet = true },
                    onRotate = { /* no-op for audio */ },
                    onToggleAudioOnly = { /* noop */ },
                    isAudioOnlyActive = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Seek
                PlayerSeekBar(
                    currentPosition = currentPosition,
                    duration = duration,
                    onSeek = onSeek,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Center playback controls
                CenterPlaybackControls(
                    isPlaying = isPlaying,
                    onPlayPause = onPlayPause,
                    onNext = onNext,
                    onPrevious = onPrevious
                )
            }

            if (showSpeedSheet) {
                SpeedBottomSheet(
                    initialSpeed = playbackSpeed,
                    onDismiss = { showSpeedSheet = false }) { speed ->
                    playbackSpeed = speed
                    onSetPlaybackSpeed(speed)
                }
            }
        }

        // Back handler
        BackHandler { onBack() }
    }
}