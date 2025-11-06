package com.playground.loose

import android.util.Log
import androidx.media3.common.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Manages playback state (position tracking, A-B loop, sleep timer)
 */
class PlaybackStateManager(
    private val player: Player,
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "PlaybackStateManager"
    }

    // Position tracking
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var positionTrackingJob: Job? = null

    // A-B Loop
    private val _abLoopState = MutableStateFlow(ABLoopState())
    val abLoopState: StateFlow<ABLoopState> = _abLoopState.asStateFlow()

    // Sleep Timer
    private val _sleepTimerRemaining = MutableStateFlow(0L)
    val sleepTimerRemaining: StateFlow<Long> = _sleepTimerRemaining.asStateFlow()
    private var sleepTimerJob: Job? = null

    /**
     * Start tracking playback position
     */
    fun startPositionTracking() {
        positionTrackingJob?.cancel()
        positionTrackingJob = scope.launch {
            while (true) {
                if (player.isPlaying) {
                    val pos = player.currentPosition
                    _currentPosition.value = pos
                    checkABLoop(pos)
                }
                delay(500)
            }
        }
    }

    /**
     * Stop tracking playback position
     */
    fun stopPositionTracking() {
        positionTrackingJob?.cancel()
        positionTrackingJob = null
    }

    /**
     * Update playback state
     */
    fun updatePlaybackState(isPlaying: Boolean, duration: Long = player.duration) {
        _isPlaying.value = isPlaying
        _duration.value = if (duration >= 0) duration else 0L

        if (isPlaying) {
            startPositionTracking()
        } else {
            stopPositionTracking()
        }
    }

    // ============ A-B Loop Functions ============

    fun setABLoopPointA() {
        val pos = player.currentPosition
        _abLoopState.value = _abLoopState.value.copy(pointA = pos)
        Log.d(TAG, "🔁 A-B Loop Point A set at ${formatTime(pos)}")
    }

    fun setABLoopPointB() {
        val pos = player.currentPosition
        val pointA = _abLoopState.value.pointA
        if (pointA != null && pos > pointA) {
            _abLoopState.value = _abLoopState.value.copy(pointB = pos, isActive = true)
            Log.d(TAG, "🔁 A-B Loop Point B set at ${formatTime(pos)}, loop activated")
        }
    }

    fun clearABLoop() {
        _abLoopState.value = ABLoopState()
        Log.d(TAG, "🔁 A-B Loop cleared")
    }

    private fun checkABLoop(currentPos: Long) {
        val loop = _abLoopState.value
        if (loop.isActive && loop.pointA != null && loop.pointB != null) {
            if (currentPos >= loop.pointB) {
                player.seekTo(loop.pointA)
            }
        }
    }

    // ============ Sleep Timer Functions ============

    fun startSleepTimer(durationMs: Long) {
        sleepTimerJob?.cancel()
        _sleepTimerRemaining.value = durationMs

        sleepTimerJob = scope.launch {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + durationMs

            while (System.currentTimeMillis() < endTime) {
                _sleepTimerRemaining.value = endTime - System.currentTimeMillis()
                delay(1000)
            }

            player.pause()
            _sleepTimerRemaining.value = 0L
            Log.d(TAG, "💤 Sleep timer ended, playback paused")
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        _sleepTimerRemaining.value = 0L
        Log.d(TAG, "💤 Sleep timer cancelled")
    }

    /**
     * Clean up resources
     */
    fun cleanup() {
        stopPositionTracking()
        cancelSleepTimer()
    }

    private fun formatTime(ms: Long): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val secs = seconds % 60
        return "${minutes}:${secs.toString().padStart(2, '0')}"
    }
}

data class ABLoopState(
    val pointA: Long? = null,
    val pointB: Long? = null,
    val isActive: Boolean = false
)