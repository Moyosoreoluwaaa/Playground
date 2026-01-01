package com.playground.loose

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import kotlinx.coroutines.delay

/**
 * Handles all video-specific playback logic
 */
class VideoPlaybackManager(
    private val player: Player
) {
    companion object {
        const val TAG = "VideoPlaybackManager"
    }

    /**
     * Play a single video
     */
    suspend fun playSingleVideo(
        video: VideoItem,
        savedPosition: Long = 0L,
        autoPlay: Boolean = true,
        repeatMode: Int = Player.REPEAT_MODE_OFF
    ): Boolean {
        return try {
            Log.d(TAG, "=== PLAY VIDEO START ===")
            Log.d(TAG, "Title: ${video.title}")
            Log.d(TAG, "Repeat Mode: $repeatMode")

            player.stop()
            player.clearMediaItems()

            val mediaItem = buildVideoMediaItem(video)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = autoPlay

            // FIXED: Apply repeat mode to player
            player.repeatMode = repeatMode
            Log.d(TAG, "🔄 Repeat mode set to: $repeatMode")

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
     * NEW: Play a playlist of video items (for RepeatMode.ALL)
     */
    suspend fun playVideoPlaylist(
        playlist: List<VideoItem>,
        startIndex: Int = 0,
        savedPosition: Long = 0L,
        repeatMode: Int = Player.REPEAT_MODE_ALL
    ): Boolean {
        return try {
            Log.d(TAG, "▶️ Playing video playlist: ${playlist.size} items, start=$startIndex")

            player.stop()
            player.clearMediaItems()

            val mediaItems = playlist.map { buildVideoMediaItem(it) }
            player.setMediaItems(mediaItems, startIndex, savedPosition.coerceAtLeast(0L))
            player.prepare()
            player.playWhenReady = true
            player.repeatMode = repeatMode

            Log.d(TAG, "✅ Video playlist loaded successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error playing video playlist", e)
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