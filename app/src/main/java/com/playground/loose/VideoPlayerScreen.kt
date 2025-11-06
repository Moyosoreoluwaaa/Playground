package com.playground.loose

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.roundToLong

@OptIn(androidx.media3.common.util.UnstableApi::class, ExperimentalMaterial3Api::class)
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
    onToggleRepeat: () -> Unit,
    onSwitchToAudio: () -> Unit,
    onBack: () -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit = {},
    viewModel: VideoPlayerViewModel, // CRITICAL FIX: Added viewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val coroutineScope = rememberCoroutineScope()

    // UI states
    var showControls by remember { mutableStateOf(true) }
    var isLandscape by remember { mutableStateOf(false) }
    var showSpeedSheet by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1f) }
    var isAudioOnlyMode by remember { mutableStateOf(false) }

    // Double-tap indicators (short flashes)
    var showForwardIndicator by remember { mutableStateOf(false) }
    var showBackwardIndicator by remember { mutableStateOf(false) }

    // Gesture state
    val gestureType = remember { mutableStateOf<GestureType?>(null) }
    var gestureValue by remember { mutableFloatStateOf(0f) }
    var showGestureOverlay by remember { mutableStateOf(false) }

    // Seek-drag preview
    var isSeekingDrag by remember { mutableStateOf(false) }
    var seekPreviewPosition by remember { mutableLongStateOf(0L) }
    var seekPreviewDeltaMs by remember { mutableLongStateOf(0L) }
    var showSeekPreview by remember { mutableStateOf(false) }

    // audio manager / brightness
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }
    val currentVolume = remember { mutableIntStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) }
    var currentBrightness by remember {
        mutableFloatStateOf(activity?.window?.attributes?.screenBrightness ?: 0.5f)
    }

    // Keep screen awake while player visible
    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            // Reset orientation
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            // Stop video if not in audio-only mode
            if (!isAudioOnlyMode) {
                player.pause()
            }
        }
    }

    // Auto-hide controls when playing - but not during gestures
    LaunchedEffect(showControls, isPlaying, showGestureOverlay, showSeekPreview) {
        if (showControls && isPlaying && !showGestureOverlay && !showSeekPreview) {
            delay(3000)
            showControls = false
        }
    }

    // System bars behavior
    LaunchedEffect(showControls) {
        activity?.window?.let { window ->
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (showControls) controller.show(WindowInsetsCompat.Type.systemBars())
            else controller.hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            // Player view with gesture handlers
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        this.player = player
                        useController = false
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(player) {
                        // Combined tap & drag gestures
                        detectTapGestures(
                            onTap = {
                                // Only toggle if no gesture active
                                if (gestureType.value == null) {
                                    showControls = !showControls
                                }
                            },
                            onDoubleTap = { offset ->
                                val w = size.width.toFloat().coerceAtLeast(1f)
                                if (offset.x < w / 2f) {
                                    // left double-tap => -10s
                                    val newPos = (player.currentPosition - 10_000L).coerceAtLeast(0L)
                                    onSeek(newPos)
                                    coroutineScope.launch {
                                        showBackwardIndicator = true
                                        delay(200)
                                        showBackwardIndicator = false
                                    }
                                } else {
                                    // right double-tap => +10s
                                    val newPos = (player.currentPosition + 10_000L).coerceAtMost(max(0L, player.duration))
                                    onSeek(newPos)
                                    coroutineScope.launch {
                                        showForwardIndicator = true
                                        delay(200)
                                        showForwardIndicator = false
                                    }
                                }
                            }
                        )
                    }
                    .pointerInput(player) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                // Determine which zone user started on:
                                val widthF = size.width.toFloat().coerceAtLeast(1f)
                                when {
                                    offset.x < widthF * 0.33f -> {
                                        // left third => brightness
                                        gestureType.value = GestureType.BRIGHTNESS
                                        showGestureOverlay = true
                                        showControls = false // Hide controls immediately
                                    }
                                    offset.x > widthF * 0.66f -> {
                                        // right third => volume
                                        gestureType.value = GestureType.VOLUME
                                        showGestureOverlay = true
                                        showControls = false // Hide controls immediately
                                    }
                                    else -> {
                                        // middle area => seek
                                        gestureType.value = GestureType.SEEK
                                        isSeekingDrag = true
                                        showSeekPreview = true
                                        showControls = false // Hide controls immediately
                                        // init preview to current position
                                        seekPreviewPosition = player.currentPosition
                                        seekPreviewDeltaMs = 0L
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                val dx = dragAmount.x
                                val dy = dragAmount.y
                                when (gestureType.value) {
                                    GestureType.VOLUME -> {
                                        // Vertical drag changes volume: upward -> increase
                                        val sensitivity = 0.005f // tweak
                                        val delta = (-dy * sensitivity * maxVolume).toInt()
                                        val newVol = (currentVolume.intValue + delta).coerceIn(0, maxVolume)
                                        if (newVol != currentVolume.intValue) {
                                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, 0)
                                            currentVolume.intValue = newVol
                                        }
                                        gestureValue = currentVolume.intValue.toFloat() / maxVolume
                                    }
                                    GestureType.BRIGHTNESS -> {
                                        val sensitivity = 0.003f
                                        val delta = -dy * sensitivity
                                        val nb = (currentBrightness + delta).coerceIn(0.01f, 1f)
                                        activity?.window?.attributes = activity.window.attributes.apply {
                                            screenBrightness = nb
                                        }
                                        currentBrightness = nb
                                        gestureValue = nb
                                    }
                                    GestureType.SEEK -> {
                                        // Horizontal drag -> seek preview
                                        val widthF = size.width.toFloat().coerceAtLeast(1f)
                                        val fraction = dx / widthF
                                        val baseWindow = max(10_000L, duration / 4L)
                                        val deltaMs = (fraction * baseWindow).roundToLong()
                                        seekPreviewDeltaMs = (seekPreviewDeltaMs + deltaMs).coerceIn(-duration, duration)
                                        seekPreviewPosition = (player.currentPosition + seekPreviewDeltaMs).coerceIn(0L, max(0L, duration))
                                    }
                                    else -> {}
                                }
                                change.consume()
                            },
                            onDragEnd = {
                                when (gestureType.value) {
                                    GestureType.SEEK -> {
                                        // commit the seek on release
                                        onSeek(seekPreviewPosition)
                                        seekPreviewDeltaMs = 0L
                                        isSeekingDrag = false
                                        showSeekPreview = false
                                    }
                                    GestureType.BRIGHTNESS, GestureType.VOLUME -> {
                                        // hide overlay
                                        showGestureOverlay = false
                                        gestureValue = 0f
                                    }
                                    else -> {}
                                }
                                gestureType.value = null
                            },
                            onDragCancel = {
                                // cleanup
                                if (isSeekingDrag) {
                                    seekPreviewDeltaMs = 0L
                                    isSeekingDrag = false
                                    showSeekPreview = false
                                }
                                showGestureOverlay = false
                                gestureType.value = null
                                gestureValue = 0f
                            }
                        )
                    }
            )

            // Double-tap small indicators (fast disappear)
            AnimatedVisibility(visible = showForwardIndicator, enter = fadeIn(), exit = fadeOut()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                    Icon(
                        Icons.Filled.Forward10,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(end = 64.dp)
                            .size(64.dp)
                    )
                }
            }

            AnimatedVisibility(visible = showBackwardIndicator, enter = fadeIn(), exit = fadeOut()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                    Icon(
                        Icons.Filled.Replay10,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(start = 64.dp)
                            .size(64.dp)
                    )
                }
            }

            // Gesture overlays: volume/brightness
            if (showGestureOverlay && (gestureType.value == GestureType.VOLUME || gestureType.value == GestureType.BRIGHTNESS)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.65f)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val (icon, label) = when (gestureType.value) {
                                GestureType.VOLUME -> Pair(
                                    Icons.AutoMirrored.Filled.VolumeUp,
                                    "${(gestureValue * 100).toInt()}%"
                                )
                                GestureType.BRIGHTNESS -> Pair(
                                    Icons.Filled.BrightnessHigh,
                                    "${(gestureValue * 100).toInt()}%"
                                )
                                else -> Pair(Icons.Filled.Info, "")
                            }
                            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(label, color = Color.White)
                        }
                    }
                }
            }

            // Seek preview overlay (shows during horizontal drag in middle)
            if (showSeekPreview) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val delta = seekPreviewDeltaMs
                            val sign = if (delta >= 0L) "+" else "-"
                            Text("$sign${formatMs(delta.absoluteValue)}", color = Color.White)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(formatMs(seekPreviewPosition), color = Color.White)
                        }
                    }
                }
            }

            // Controls overlay (top + center + bottom seek) - Only show when not gesturing
            AnimatedVisibility(
                visible = showControls && !showGestureOverlay && !showSeekPreview,
                enter = fadeIn(tween(180)),
                exit = fadeOut(tween(140))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.36f))
                ) {
                    // Top bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionIconButton(
                            onClick = onBack,
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentDescription = "Back"
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            currentVideo?.title ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Centered playback controls
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(28.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CenterPlaybackControls(
                            isPlaying = isPlaying,
                            onPlayPause = onPlayPause,
                            onNext = onNext,
                            onPrevious = onPrevious
                        )
                    }

                    // Bottom seek/time
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) {
                        ActionRowAboveSeek(
                            repeatMode = repeatMode,
                            onToggleRepeat = onToggleRepeat,
                            onShowSpeed = { showSpeedSheet = true },
                            onRotate = {
                                isLandscape = !isLandscape
                                activity?.requestedOrientation = if (isLandscape)
                                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                else
                                    ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
                            },
                            // CRITICAL FIX: The logic inside onToggleAudioOnly has been updated
                            onToggleAudioOnly = {
                                coroutineScope.launch {
                                    // CRITICAL: Call ViewModel function first to update external state
                                    viewModel.playVideoAsAudio()

                                    // CRITICAL: Wait for state to propagate before navigating
                                    delay(100)

                                    isAudioOnlyMode = true
                                    onSwitchToAudio()
                                }
                            },
                            isAudioOnlyActive = isAudioOnlyMode,
                            playbackSpeed = playbackSpeed,
                            isLandscape = isLandscape,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(formatMs(currentPosition), color = Color.White)
                            Slider(
                                value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                                onValueChange = { v -> onSeek((v * duration).toLong()) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(formatMs(duration), color = Color.White)
                        }
                    }
                }
            }

            // Speed bottom sheet
            if (showSpeedSheet) {
                SpeedBottomSheet(
                    initialSpeed = playbackSpeed,
                    onDismiss = { showSpeedSheet = false }
                ) { speed ->
                    playbackSpeed = speed
                    onSetPlaybackSpeed(speed)
                }
            }
        }
    }

    // Back handler - Stop video and reset orientation
    BackHandler {
        // Reset to portrait
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        // Stop video if not in audio-only mode
        if (!isAudioOnlyMode) {
            player.pause()
        }

        onBack()
    }
}

private fun formatMs(ms: Long): String {
    val absoluteMs = ms.absoluteValue
    if (absoluteMs <= 0) return "00:00"

    val totalSeconds = absoluteMs / 1000
    val hasHours = totalSeconds >= 3600

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hasHours) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}

enum class GestureType { VOLUME, BRIGHTNESS, SEEK }