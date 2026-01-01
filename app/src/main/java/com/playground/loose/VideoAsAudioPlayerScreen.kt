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
import androidx.media3.common.util.UnstableApi

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoAsAudioPlayerScreen(
    videoViewModel: VideoPlayerViewModel,
    currentVideo: VideoItem?,
    onBack: () -> Unit,
    onNavigateToVideoPlayer: () -> Unit
) {
    val isPlaying by videoViewModel.isPlaying.collectAsState()
    val currentPosition by videoViewModel.currentPosition.collectAsState()
    val duration by videoViewModel.duration.collectAsState()
    val repeatMode by videoViewModel.repeatMode.collectAsState()
    val abLoopState by videoViewModel.stateManager.abLoopState.collectAsState()
    val sleepTimerRemaining by videoViewModel.stateManager.sleepTimerRemaining.collectAsState()
    val playbackSpeed by videoViewModel.playbackSpeed.collectAsState()

    val context = LocalContext.current

    var bgColors by remember { mutableStateOf(listOf(Color(0xFF121212), Color(0xFF000000))) }
    var showSpeedSheet by remember { mutableStateOf(false) }
    var showSleepTimerSheet by remember { mutableStateOf(false) }
    var showABLoopSheet by remember { mutableStateOf(false) }

    val displayTitle = currentVideo?.title ?: "No video"
    val thumbnailUri = currentVideo?.thumbnailUri

    LaunchedEffect(thumbnailUri) {
        thumbnailUri?.let { uri ->
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
                // Video Thumbnail (larger for video)
                AlbumArtwork(
                    albumArtUri = thumbnailUri,
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Video Info
                Text(
                    text = displayTitle,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Video (Audio Only)",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Enhanced Action Row for Video-as-Audio
                VideoAsAudioActionRow(
                    repeatMode = repeatMode,
                    onToggleRepeat = videoViewModel::toggleRepeatMode,
                    onShowSpeed = { showSpeedSheet = true },
                    onShowSleepTimer = { showSleepTimerSheet = true },
                    onShowABLoop = { showABLoopSheet = true },
                    onReturnToVideo = {
                        Log.d("VideoAsAudioScreen", "🎥 Return to Video button clicked")
                        onNavigateToVideoPlayer()
                        videoViewModel.returnToVideoPlayer()
                    },
                    abLoopActive = abLoopState.isActive,
                    sleepTimerActive = sleepTimerRemaining > 0,
                    playbackSpeed = playbackSpeed,
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
                                onClick = { videoViewModel.stateManager.clearABLoop() },
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
                    onSeek = videoViewModel::seekTo,
                    abLoopState = abLoopState,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Center Playback Controls
                CenterPlaybackControls(
                    isPlaying = isPlaying,
                    onPlayPause = videoViewModel::playPause,
                    onNext = videoViewModel::playNext,
                    onPrevious = videoViewModel::playPrevious
                )
            }
        }

        // Bottom Sheets
        if (showSpeedSheet) {
            SpeedBottomSheet(
                initialSpeed = playbackSpeed,
                onDismiss = { showSpeedSheet = false }
            ) { speed ->
                videoViewModel.setPlaybackSpeed(speed)
            }
        }

        if (showSleepTimerSheet) {
            SleepTimerBottomSheet(
                currentTimerMs = sleepTimerRemaining,
                onDismiss = { showSleepTimerSheet = false },
                onSetTimer = { durationMs ->
                    videoViewModel.stateManager.startSleepTimer(durationMs)
                    showSleepTimerSheet = false
                },
                onCancelTimer = {
                    videoViewModel.stateManager.cancelSleepTimer()
                    showSleepTimerSheet = false
                }
            )
        }

        if (showABLoopSheet) {
            ABLoopBottomSheet(
                currentState = abLoopState,
                onDismiss = { showABLoopSheet = false },
                onSetPointA = { videoViewModel.stateManager.setABLoopPointA() },
                onSetPointB = { videoViewModel.stateManager.setABLoopPointB() },
                onClear = {
                    videoViewModel.stateManager.clearABLoop()
                    showABLoopSheet = false
                }
            )
        }

        BackHandler { onBack() }
    }
}

@Composable
fun VideoAsAudioActionRow(
    modifier: Modifier = Modifier,
    repeatMode: RepeatMode,
    onToggleRepeat: () -> Unit,
    onShowSpeed: () -> Unit,
    onShowSleepTimer: () -> Unit,
    onShowABLoop: () -> Unit,
    onReturnToVideo: () -> Unit,
    abLoopActive: Boolean,
    sleepTimerActive: Boolean,
    playbackSpeed: Float,
    actionBgColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    activeColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(modifier = modifier) {
        // First Row: Repeat, Speed, A-B Loop, Return to Video
        Row(
            modifier = Modifier.fillMaxWidth(),
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

            ActionIconButton(
                onClick = onShowSpeed,
                icon = Icons.Filled.Speed,
                backgroundColor = if (playbackSpeed != 1f) activeColor else actionBgColor,
                contentDescription = "Speed"
            )

            ActionIconButton(
                onClick = onShowABLoop,
                icon = Icons.Default.RepeatOn,
                backgroundColor = if (abLoopActive) activeColor else actionBgColor,
                contentDescription = "A-B Loop"
            )

            // Return to Video button
            ActionIconButton(
                onClick = onReturnToVideo,
                icon = Icons.Filled.VideoLibrary,
                backgroundColor = activeColor,
                contentDescription = "Return to Video"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Second Row: Sleep Timer (centered)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionIconButton(
                onClick = onShowSleepTimer,
                icon = Icons.Default.Bedtime,
                backgroundColor = if (sleepTimerActive) activeColor else actionBgColor,
                contentDescription = "Sleep Timer"
            )
        }
    }
}