package com.playground.loose

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.playground.loose.VideoItem
import kotlinx.coroutines.delay

/**
 * Handles all video-specific playback logic
 */
class VideoPlaybackManager(
    private val player: Player
) {
    companion object {
        private const val TAG = "VideoPlaybackManager"
    }

    /**
     * Play a single video
     */
    suspend fun playSingleVideo(
        video: VideoItem,
        savedPosition: Long = 0L,
        autoPlay: Boolean = true
    ): Boolean {
        return try {
            Log.d(TAG, "=== PLAY VIDEO START ===")
            Log.d(TAG, "Title: ${video.title}")

            player.stop()
            player.clearMediaItems()

            val mediaItem = buildVideoMediaItem(video)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = autoPlay

            // Wait for player to be ready
            var attempts = 0
            while (player.playbackState != Player.STATE_READY && attempts < 20) {
                delay(100)
                attempts++
            }

            if (player.playbackState == Player.STATE_READY && savedPosition > 0) {
                val validPosition = savedPosition.coerceIn(0, player.duration)
                player.seekTo(validPosition)
                Log.d(TAG, "⏩ Seeking to saved position: ${formatTime(validPosition)}")
            }

            Log.d(TAG, "=== PLAY VIDEO END ===")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error playing video", e)
            false
        }
    }

    /**
     * Build MediaItem for video
     */
    private fun buildVideoMediaItem(video: VideoItem): MediaItem {
        return MediaItem.Builder()
            .setUri(video.uri)
            .setMediaId(video.id.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(video.title)
                    .build()
            )
            .build()
    }

    /**
     * Get current video item from player
     */
    fun getCurrentVideoId(): Long? {
        return player.currentMediaItem?.mediaId?.toLongOrNull()
    }

    private fun formatTime(ms: Long): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val secs = seconds % 60
        return "${minutes}:${secs.toString().padStart(2, '0')}"
    }
}
