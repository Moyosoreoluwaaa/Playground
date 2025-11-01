package com.playground.loose

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.provider.Settings
import android.view.WindowManager
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import kotlin.math.abs

@UnstableApi
@Composable
fun VideoPlayerScreen(
    player: Player,
    currentVideo: VideoItem?,
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
    onSwitchToAudio: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var showControls by remember { mutableStateOf(true) }
    var isFullscreen by remember { mutableStateOf(false) }

    // Gesture states
    var gestureType by remember { mutableStateOf<GestureType?>(null) }
    var gestureValue by remember { mutableFloatStateOf(0f) }
    var showGestureIndicator by remember { mutableStateOf(false) }

    // Audio and brightness managers
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }
    val currentVolume = remember { mutableIntStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) }

    var currentBrightness by remember {
        mutableFloatStateOf(
            activity?.window?.attributes?.screenBrightness ?: -1f
        )
    }

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    LaunchedEffect(showGestureIndicator) {
        if (showGestureIndicator) {
            kotlinx.coroutines.delay(1000)
            showGestureIndicator = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        gestureType = GestureType.SEEK
                        showGestureIndicator = true
                    },
                    onDragEnd = {
                        // Apply seek
                        if (gestureType == GestureType.SEEK) {
                            val seekDelta = (gestureValue * duration / size.width).toLong()
                            val newPosition = (currentPosition + seekDelta).coerceIn(0, duration)
                            onSeek(newPosition)
                        }
                        gestureType = null
                        gestureValue = 0f
                        showGestureIndicator = false
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        gestureValue += dragAmount
                        change.consume()
                    }
                )
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        // Left side = brightness, right side = volume
                        gestureType = if (offset.x < size.width / 2) {
                            GestureType.BRIGHTNESS
                        } else {
                            GestureType.VOLUME
                        }
                        showGestureIndicator = true
                    },
                    onDragEnd = {
                        gestureType = null
                        gestureValue = 0f
                        showGestureIndicator = false
                    },
                    onVerticalDrag = { change, dragAmount ->
                        when (gestureType) {
                            GestureType.VOLUME -> {
                                val volumeDelta = (-dragAmount / size.height * maxVolume).toInt()
                                val newVolume = (currentVolume.intValue + volumeDelta).coerceIn(0, maxVolume)
                                if (newVolume != currentVolume.intValue) {
                                    audioManager.setStreamVolume(
                                        AudioManager.STREAM_MUSIC,
                                        newVolume,
                                        0
                                    )
                                    currentVolume.intValue = newVolume
                                    gestureValue = newVolume.toFloat() / maxVolume
                                }
                            }
                            GestureType.BRIGHTNESS -> {
                                val brightnessDelta = -dragAmount / size.height
                                val newBrightness = (currentBrightness + brightnessDelta).coerceIn(0f, 1f)
                                activity?.window?.attributes = activity.window.attributes.apply {
                                    screenBrightness = newBrightness
                                }
                                currentBrightness = newBrightness
                                gestureValue = newBrightness
                            }
                            else -> {}
                        }
                        change.consume()
                    }
                )
            }
    ) {
        // Video Player
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                    setOnClickListener {
                        showControls = !showControls
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Gesture Indicator Overlay
        AnimatedVisibility(
            visible = showGestureIndicator,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (gestureType) {
                        GestureType.SEEK -> {
                            val seekSeconds = (gestureValue * duration / 1000).toInt()
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (gestureValue > 0) Icons.Filled.FastForward else Icons.Filled.FastRewind,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${if (gestureValue > 0) "+" else ""}${seekSeconds}s",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                        GestureType.VOLUME -> {
                            val volumePercent = (gestureValue * 100).toInt()
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = when {
                                        volumePercent == 0 -> Icons.Filled.VolumeOff
                                        volumePercent < 50 -> Icons.Filled.VolumeDown
                                        else -> Icons.Filled.VolumeUp
                                    },
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "$volumePercent%",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                        GestureType.BRIGHTNESS -> {
                            val brightnessPercent = (gestureValue * 100).toInt()
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (brightnessPercent < 50) Icons.Filled.BrightnessLow else Icons.Filled.BrightnessHigh,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "$brightnessPercent%",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                        null -> {}
                    }
                }
            }
        }

        // Video Controls Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    currentVideo?.let {
                        Text(
                            text = it.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                        )
                    }

                    IconButton(onClick = onToggleRepeat) {
                        Icon(
                            imageVector = when (repeatMode) {
                                RepeatMode.OFF -> Icons.Filled.Repeat
                                RepeatMode.ONE -> Icons.Filled.RepeatOne
                                RepeatMode.ALL -> Icons.Filled.Repeat
                            },
                            contentDescription = "Repeat",
                            tint = if (repeatMode != RepeatMode.OFF) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.White
                            }
                        )
                    }

                    IconButton(onClick = onSwitchToAudio) {
                        Icon(
                            imageVector = Icons.Filled.Headphones,
                            contentDescription = "Audio only",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = { isFullscreen = !isFullscreen }) {
                        Icon(
                            imageVector = if (isFullscreen) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = Color.White
                        )
                    }
                }

                // Center controls
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPrevious) {
                        Icon(
                            imageVector = Icons.Filled.SkipPrevious,
                            contentDescription = "Previous",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    IconButton(onClick = onSkipBackward) {
                        Icon(
                            imageVector = Icons.Filled.Replay10,
                            contentDescription = "Rewind",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    FloatingActionButton(
                        onClick = onPlayPause,
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(onClick = onSkipForward) {
                        Icon(
                            imageVector = Icons.Filled.Forward10,
                            contentDescription = "Forward",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    IconButton(onClick = onNext) {
                        Icon(
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Bottom bar with seek bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDuration(currentPosition),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Slider(
                            value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                            onValueChange = { value ->
                                onSeek((value * duration).toLong())
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Text(
                            text = formatDuration(duration),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

enum class GestureType {
    SEEK, VOLUME, BRIGHTNESS
}