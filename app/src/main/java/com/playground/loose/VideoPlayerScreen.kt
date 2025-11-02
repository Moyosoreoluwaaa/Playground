package com.playground.loose

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.view.WindowManager
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
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
    var isLandscape by remember { mutableStateOf(false) }
    var showSpeedSheet by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1f) }
    var isAudioOnlyMode by remember { mutableStateOf(false) }

    // Gesture states
    var gestureType by remember { mutableStateOf<GestureType?>(null) }
    var gestureValue by remember { mutableFloatStateOf(0f) }
    var showGestureIndicator by remember { mutableStateOf(false) }

    // Double tap animation states
    var showForwardAnimation by remember { mutableStateOf(false) }
    var showBackwardAnimation by remember { mutableStateOf(false) }

    // Audio and brightness managers
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }
    val currentVolume = remember { mutableIntStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) }

    var currentBrightness by remember {
        mutableFloatStateOf(
            activity?.window?.attributes?.screenBrightness ?: 0.5f
        )
    }

    // Keep screen awake while playing
    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Auto-hide controls after 3 seconds with smooth animation
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(3000)
            showControls = false
        }
    }

    // Smooth system bars hide/show
    LaunchedEffect(showControls) {
        activity?.window?.let { window ->
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (showControls) {
                controller.show(WindowInsetsCompat.Type.systemBars())
            } else {
                controller.hide(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Orientation control
    DisposableEffect(Unit) {
        val originalOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT

        onDispose {
            activity?.requestedOrientation = originalOrientation
                ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            activity?.window?.let { window ->
                WindowCompat.getInsetsController(window, window.decorView)
                    .show(WindowInsetsCompat.Type.systemBars())
            }

            // Pause video when leaving if not in audio-only mode
            if (!isAudioOnlyMode && isPlaying) {
                onPlayPause()
            }
        }
    }

    LaunchedEffect(showGestureIndicator) {
        if (showGestureIndicator) {
            delay(1000)
            showGestureIndicator = false
        }
    }

    LaunchedEffect(showForwardAnimation) {
        if (showForwardAnimation) {
            delay(500)
            showForwardAnimation = false
        }
    }

    LaunchedEffect(showBackwardAnimation) {
        if (showBackwardAnimation) {
            delay(500)
            showBackwardAnimation = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video Player
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            showControls = !showControls
                        },
                        onDoubleTap = { offset ->
                            // Left side = rewind, right side = forward
                            if (offset.x < size.width / 2) {
                                // Rewind 10 seconds
                                val newPos = (currentPosition - 10000).coerceAtLeast(0)
                                onSeek(newPos)
                                showBackwardAnimation = true
                            } else {
                                // Forward 10 seconds
                                val newPos = (currentPosition + 10000).coerceAtMost(duration)
                                onSeek(newPos)
                                showForwardAnimation = true
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = { offset ->
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
                                    val sensitivity = 0.01f
                                    val volumeDelta = (-dragAmount * sensitivity * maxVolume).toInt()
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
                                    val sensitivity = 0.002f
                                    val brightnessDelta = -dragAmount * sensitivity
                                    val newBrightness = (currentBrightness + brightnessDelta).coerceIn(0.01f, 1f)
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
        )

        // Double tap animations
        AnimatedVisibility(
            visible = showForwardAnimation,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 48.dp),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Forward10,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "+10s",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showBackwardAnimation,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 48.dp),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Replay10,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "-10s",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

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
                                    imageVector = if (brightnessPercent < 50)
                                        Icons.Filled.BrightnessLow
                                    else
                                        Icons.Filled.BrightnessHigh,
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
                        else -> {}
                    }
                }
            }
        }

        // Video Controls Overlay - Smooth fade animation
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
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
                            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                            maxLines = 1
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Speed control
                        IconButton(onClick = { showSpeedSheet = true }) {
                            Icon(
                                imageVector = Icons.Filled.Speed,
                                contentDescription = "Playback speed",
                                tint = Color.White
                            )
                        }

                        // Rotation button
                        IconButton(onClick = {
                            isLandscape = !isLandscape
                            activity?.requestedOrientation = if (isLandscape) {
                                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            } else {
                                ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
                            }
                        }) {
                            Icon(
                                imageVector = if (isLandscape)
                                    Icons.Filled.ScreenRotation
                                else
                                    Icons.Filled.ScreenLockRotation,
                                contentDescription = "Rotate",
                                tint = Color.White
                            )
                        }

                        // Audio-only mode (background play)
                        IconButton(
                            onClick = {
                                isAudioOnlyMode = !isAudioOnlyMode
                                if (isAudioOnlyMode) {
                                    onSwitchToAudio()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Headphones,
                                contentDescription = "Audio only",
                                tint = if (isAudioOnlyMode)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.White
                            )
                        }
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

    // Playback speed bottom sheet
    if (showSpeedSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSpeedSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Playback Speed",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val speeds = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f, 2.5f, 3f)

                speeds.forEach { speed ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = "${speed}x",
                                color = if (playbackSpeed == speed)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        },
                        trailingContent = {
                            if (playbackSpeed == speed) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        modifier = Modifier.clickable {
                            playbackSpeed = speed
                            player.setPlaybackSpeed(speed)
                            showSpeedSheet = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

enum class GestureType {
    VOLUME, BRIGHTNESS
}