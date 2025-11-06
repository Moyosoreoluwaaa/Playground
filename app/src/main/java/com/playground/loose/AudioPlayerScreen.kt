package com.playground.loose

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.media3.common.util.UnstableApi


@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedAudioPlayerScreen(
    audioViewModel: AudioPlayerViewModel,
    videoViewModel: VideoPlayerViewModel,
    isVideoAsAudioMode: Boolean,
    currentVideo: VideoItem?,
    onBack: () -> Unit,
    onNavigateToVideoPlayer: () -> Unit = {}
) {
    val currentAudio by audioViewModel.currentAudioItem.collectAsState()
    val isPlaying by audioViewModel.isPlaying.collectAsState()
    val currentPosition by audioViewModel.currentPosition.collectAsState()
    val duration by audioViewModel.duration.collectAsState()
    val repeatMode by audioViewModel.repeatMode.collectAsState()
    val abLoopState by audioViewModel.abLoopState.collectAsState()
    val sleepTimerRemaining by audioViewModel.sleepTimerRemaining.collectAsState()
    val playbackSpeed by audioViewModel.playbackSpeed.collectAsState()

    val context = LocalContext.current

    // DEBUG: Add logging
    LaunchedEffect(isVideoAsAudioMode, currentVideo, currentAudio) {
        Log.d("AudioPlayerScreen", "=== STATE UPDATE ===")
        Log.d("AudioPlayerScreen", "isVideoAsAudioMode: $isVideoAsAudioMode")
        Log.d("AudioPlayerScreen", "currentVideo: ${currentVideo?.title}")
        Log.d("AudioPlayerScreen", "currentAudio: ${currentAudio?.title}")
    }

    var bgColors by remember { mutableStateOf(listOf(Color(0xFF121212), Color(0xFF000000))) }
    var showSpeedSheet by remember { mutableStateOf(false) }
    var showSleepTimerSheet by remember { mutableStateOf(false) }
    var showABLoopSheet by remember { mutableStateOf(false) }

    // Get the correct title and artwork based on mode
    val displayTitle = remember(isVideoAsAudioMode, currentVideo, currentAudio) {
        if (isVideoAsAudioMode && currentVideo != null) {
            currentVideo.title
        } else currentAudio?.title ?: "No track"
    }

    val displayArtist = remember(isVideoAsAudioMode, currentAudio) {
        if (isVideoAsAudioMode) null else currentAudio?.artist
    }

    val displayAlbum = remember(isVideoAsAudioMode, currentAudio) {
        if (isVideoAsAudioMode) null else currentAudio?.album
    }

    // Extract album art URI from correct source
    val albumArtUri = remember(isVideoAsAudioMode, currentVideo, currentAudio) {
        if (isVideoAsAudioMode && currentVideo != null) {
            currentVideo.thumbnailUri
        } else {
            currentAudio?.albumArtUri
        }
    }

    LaunchedEffect(albumArtUri) {
        albumArtUri?.let { uri ->
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
        ) {
            // Top bar with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionIconButton(
                    onClick = onBack,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentDescription = "Back"
                )

                Spacer(modifier = Modifier.weight(1f))

                // Sleep Timer Indicator
                if (sleepTimerRemaining > 0) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BedtimeOff,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = formatDuration(sleepTimerRemaining),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                // Album Art
                AlbumArtwork(
                    albumArtUri = albumArtUri,
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Track Info
                Text(
                    text = displayTitle,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (isVideoAsAudioMode) {
                    Text(
                        text = "Video (Audio Only)",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    displayArtist?.let {
                        Text(
                            text = it,
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    displayAlbum?.let {
                        Text(
                            text = it,
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Enhanced Action Row
                EnhancedActionRow(
                    repeatMode = repeatMode,
                    onToggleRepeat = audioViewModel::toggleRepeatMode,
                    onShowSpeed = { showSpeedSheet = true },
                    onShowSleepTimer = { showSleepTimerSheet = true },
                    onShowABLoop = { showABLoopSheet = true },
                    abLoopActive = abLoopState.isActive,
                    sleepTimerActive = sleepTimerRemaining > 0,
                    playbackSpeed = playbackSpeed,
                    isVideoAsAudioMode = isVideoAsAudioMode,
                    onReturnToVideo = {
                        Log.d("AudioPlayerScreen", "🔵 Return to Video button clicked")
                        // Navigate FIRST before updating state to avoid flash
                        onNavigateToVideoPlayer()
                        Log.d("AudioPlayerScreen", "🔵 Navigation callback executed")
                        // Update state after navigation starts
                        videoViewModel.returnToVideoPlayer()
                        Log.d("AudioPlayerScreen", "🔵 ViewModel state updated")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // A-B Loop Indicator
                if (abLoopState.isActive && abLoopState.pointA != null && abLoopState.pointB != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔁 Loop: ${formatDuration(abLoopState.pointA!!)} → ${
                                    formatDuration(abLoopState.pointB!!)
                                }",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            IconButton(
                                onClick = audioViewModel::clearABLoop,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear loop",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Seek Bar with A-B markers
                EnhancedSeekBar(
                    currentPosition = currentPosition,
                    duration = duration,
                    onSeek = audioViewModel::seekTo,
                    abLoopState = abLoopState,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Center Playback Controls
                CenterPlaybackControls(
                    isPlaying = isPlaying,
                    onPlayPause = audioViewModel::playPause,
                    onNext = audioViewModel::playNext,
                    onPrevious = audioViewModel::playPrevious
                )
            }
        }

        // Bottom Sheets
        if (showSpeedSheet) {
            SpeedBottomSheet(
                initialSpeed = playbackSpeed,
                onDismiss = { showSpeedSheet = false }
            ) { speed ->
                audioViewModel.setPlaybackSpeed(speed)
            }
        }

        if (showSleepTimerSheet) {
            SleepTimerBottomSheet(
                currentTimerMs = sleepTimerRemaining,
                onDismiss = { showSleepTimerSheet = false },
                onSetTimer = { durationMs ->
                    audioViewModel.startSleepTimer(durationMs)
                    showSleepTimerSheet = false
                },
                onCancelTimer = {
                    audioViewModel.cancelSleepTimer()
                    showSleepTimerSheet = false
                }
            )
        }

        if (showABLoopSheet) {
            ABLoopBottomSheet(
                currentState = abLoopState,
                onDismiss = { showABLoopSheet = false },
                onSetPointA = audioViewModel::setABLoopPointA,
                onSetPointB = audioViewModel::setABLoopPointB,
                onClear = {
                    audioViewModel.clearABLoop()
                    showABLoopSheet = false
                }
            )
        }

        BackHandler { onBack() }
    }
}

// Keep all existing helper composables (EnhancedActionRow, EnhancedSeekBar, etc.)
// They remain unchanged
@Composable
fun EnhancedActionRow(
    modifier: Modifier = Modifier,
    repeatMode: RepeatMode,
    onToggleRepeat: () -> Unit,
    onShowSpeed: () -> Unit,
    onShowSleepTimer: () -> Unit,
    onShowABLoop: () -> Unit,
    abLoopActive: Boolean,
    sleepTimerActive: Boolean,
    playbackSpeed: Float,
    isVideoAsAudioMode: Boolean,
    onReturnToVideo: () -> Unit,
    actionBgColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    activeColor: Color = MaterialTheme.colorScheme.primary
) {
    // DEBUG: Log when this composable renders
    LaunchedEffect(isVideoAsAudioMode) {
        Log.d("EnhancedActionRow", "Rendering with isVideoAsAudioMode=$isVideoAsAudioMode")
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        // Speed
        ActionIconButton(
            onClick = onShowSpeed,
            icon = Icons.Filled.Speed,
            backgroundColor = if (playbackSpeed != 1f) activeColor else actionBgColor,
            contentDescription = "Speed"
        )

        // A-B Loop
        ActionIconButton(
            onClick = onShowABLoop,
            icon = Icons.Default.RepeatOn,
            backgroundColor = if (abLoopActive) activeColor else actionBgColor,
            contentDescription = "A-B Loop"
        )

        // NEW: Conditionally show either Sleep Timer OR Return to Video button
        if (isVideoAsAudioMode) {
            Log.d("EnhancedActionRow", "🎥 Showing Return to Video button")
            // Show Video Mode button when listening to video as audio
            ActionIconButton(
                onClick = {
                    Log.d("EnhancedActionRow", "🎥 Return to Video button CLICKED")
                    onReturnToVideo()
                },
                icon = Icons.Filled.VideoLibrary,
                backgroundColor = activeColor,
                contentDescription = "Return to Video"
            )
        } else {
            Log.d("EnhancedActionRow", "😴 Showing Sleep Timer button")
            // Show Sleep Timer for regular audio
            ActionIconButton(
                onClick = onShowSleepTimer,
                icon = Icons.Default.Bedtime,
                backgroundColor = if (sleepTimerActive) activeColor else actionBgColor,
                contentDescription = "Sleep Timer"
            )
        }
    }
}

@Composable
fun EnhancedSeekBar(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    abLoopState: ABLoopState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Main Slider
            Slider(
                modifier = Modifier
                    .fillMaxWidth()
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

            // A-B Loop Markers
            if (abLoopState.pointA != null && duration > 0) {
                val pointAFraction = abLoopState.pointA.toFloat() / duration
                Box(
                    modifier = Modifier
                        .fillMaxWidth(pointAFraction)
                        .height(4.dp)
                        .background(Color.Yellow.copy(alpha = 0.7f))
                        .align(Alignment.CenterStart)
                )
            }

            if (abLoopState.pointB != null && duration > 0) {
                val pointBFraction = abLoopState.pointB.toFloat() / duration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = (1f - pointBFraction) * 100.dp)
                        .height(4.dp)
                        .background(Color.Yellow.copy(alpha = 0.7f))
                        .align(Alignment.CenterEnd)
                )
            }
        }

        // Time labels
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(currentPosition),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTimerBottomSheet(
    currentTimerMs: Long,
    onDismiss: () -> Unit,
    onSetTimer: (Long) -> Unit,
    onCancelTimer: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Sleep Timer",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (currentTimerMs > 0) {
                Text(
                    text = "Timer active: ${formatDuration(currentTimerMs)} remaining",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = onCancelTimer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel Timer")
                }
            } else {
                val presets = listOf(
                    "5 minutes" to 5 * 60 * 1000L,
                    "15 minutes" to 15 * 60 * 1000L,
                    "30 minutes" to 30 * 60 * 1000L,
                    "45 minutes" to 45 * 60 * 1000L,
                    "1 hour" to 60 * 60 * 1000L,
                    "2 hours" to 120 * 60 * 1000L
                )

                presets.forEach { (label, durationMs) ->
                    androidx.compose.material3.ListItem(
                        headlineContent = { Text(label) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSetTimer(durationMs) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ABLoopBottomSheet(
    currentState: ABLoopState,
    onDismiss: () -> Unit,
    onSetPointA: () -> Unit,
    onSetPointB: () -> Unit,
    onClear: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "A-B Loop",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Set loop points to repeat a specific section",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Point A
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Point A (Start)", style = MaterialTheme.typography.titleMedium)
                    if (currentState.pointA != null) {
                        Text(
                            formatDuration(currentState.pointA),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Button(onClick = onSetPointA) {
                    Text(if (currentState.pointA == null) "Set A" else "Update A")
                }
            }

            // Point B
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Point B (End)", style = MaterialTheme.typography.titleMedium)
                    if (currentState.pointB != null) {
                        Text(
                            formatDuration(currentState.pointB),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Button(
                    onClick = onSetPointB,
                    enabled = currentState.pointA != null
                ) {
                    Text(if (currentState.pointB == null) "Set B" else "Update B")
                }
            }

            // Status and Clear
            if (currentState.isActive) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "✓ Loop active",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Button(
                    onClick = onClear,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Clear Loop")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}