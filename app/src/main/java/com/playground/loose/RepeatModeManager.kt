package com.playground.loose

import android.util.Log
import androidx.media3.common.Player
import com.playground.loose.RepeatMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages repeat mode for playback
 */
class RepeatModeManager(private val player: Player) {
    companion object {
        private const val TAG = "RepeatModeManager"
    }

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    /**
     * Toggle repeat mode: OFF -> ALL -> ONE -> OFF
     */
    fun toggleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }

        applyRepeatModeToPlayer()
        Log.d(TAG, "🔄 Repeat mode: ${_repeatMode.value}")
    }

    /**
     * Set specific repeat mode
     */
    fun setRepeatMode(mode: RepeatMode) {
        _repeatMode.value = mode
        applyRepeatModeToPlayer()
    }

    /**
     * Apply current repeat mode to player
     */
    private fun applyRepeatModeToPlayer() {
        player.repeatMode = when (_repeatMode.value) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
        }
    }

    /**
     * Get current repeat mode
     */
    fun getCurrentRepeatMode(): RepeatMode = _repeatMode.value
}
