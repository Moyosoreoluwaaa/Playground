package com.playground.loose

import android.util.Log
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player

object PlaybackDebugger {
    private const val TAG = "PlaybackDebugger"

    fun attachToPlayer(player: Player) {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val state = when (playbackState) {
                    Player.STATE_IDLE -> "IDLE"
                    Player.STATE_BUFFERING -> "BUFFERING"
                    Player.STATE_READY -> "READY"
                    Player.STATE_ENDED -> "ENDED"
                    else -> "UNKNOWN"
                }
                Log.d(TAG, "Playback State: $state")
                Log.d(TAG, "PlayWhenReady: ${player.playWhenReady}")
                Log.d(TAG, "IsPlaying: ${player.isPlaying}")
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d(TAG, "IsPlaying Changed: $isPlaying")
                Log.d(TAG, "Current Position: ${player.currentPosition}ms")
                Log.d(TAG, "Duration: ${player.duration}ms")
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "Player Error: ${error.errorCodeName}")
                Log.e(TAG, "Error Message: ${error.message}")
                Log.e(TAG, "Cause: ${error.cause?.message}")
                error.printStackTrace()
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                val reasonStr = when (reason) {
                    Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST -> "USER_REQUEST"
                    Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS -> "AUDIO_FOCUS_LOSS"
                    Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY -> "AUDIO_BECOMING_NOISY"
                    Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE -> "REMOTE"
                    else -> "UNKNOWN"
                }
                Log.d(TAG, "PlayWhenReady Changed: $playWhenReady (Reason: $reasonStr)")
            }

            override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                Log.d(TAG, "Media Item Transition: ${mediaItem?.mediaMetadata?.title}")
                Log.d(TAG, "Media URI: ${mediaItem?.localConfiguration?.uri}")
            }
        })
    }

    fun logPlayerState(player: Player, tag: String = "PlayerState") {
        Log.d(TAG, "=== $tag ===")
        Log.d(TAG, "PlaybackState: ${getPlaybackStateName(player.playbackState)}")
        Log.d(TAG, "PlayWhenReady: ${player.playWhenReady}")
        Log.d(TAG, "IsPlaying: ${player.isPlaying}")
        Log.d(TAG, "CurrentPosition: ${player.currentPosition}ms")
        Log.d(TAG, "Duration: ${player.duration}ms")
        Log.d(TAG, "BufferedPosition: ${player.bufferedPosition}ms")
        Log.d(TAG, "CurrentMediaItem: ${player.currentMediaItem?.mediaMetadata?.title}")
        Log.d(TAG, "MediaItemCount: ${player.mediaItemCount}")
        Log.d(TAG, "===============")
    }

    private fun getPlaybackStateName(state: Int): String = when (state) {
        Player.STATE_IDLE -> "IDLE"
        Player.STATE_BUFFERING -> "BUFFERING"
        Player.STATE_READY -> "READY"
        Player.STATE_ENDED -> "ENDED"
        else -> "UNKNOWN($state)"
    }
}

// Extension functions for easy debugging
fun Player.logState(tag: String = "PlayerState") {
    PlaybackDebugger.logPlayerState(this, tag)
}

fun Player.attachDebugger() {
    PlaybackDebugger.attachToPlayer(this)
}