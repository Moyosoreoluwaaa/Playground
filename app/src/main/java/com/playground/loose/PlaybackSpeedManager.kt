package com.playground.loose

import android.util.Log
import androidx.media3.common.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages playback speed
 */
class PlaybackSpeedManager(private val player: Player) {
    companion object {
        private const val TAG = "PlaybackSpeedManager"
    }

    private val _playbackSpeed = MutableStateFlow(1f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    /**
     * Set playback speed
     */
    fun setSpeed(speed: Float) {
        val validSpeed = speed.coerceIn(0.25f, 2.0f)
        _playbackSpeed.value = validSpeed
        player.setPlaybackSpeed(validSpeed)
        Log.d(TAG, "⏩ Playback speed set to ${validSpeed}x")
    }

    /**
     * Get current playback speed
     */
    fun getCurrentSpeed(): Float = _playbackSpeed.value

    /**
     * Reset to normal speed
     */
    fun resetSpeed() {
        setSpeed(1f)
    }
}