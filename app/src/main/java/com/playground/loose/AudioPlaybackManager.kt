package com.loose.mediaplayer.playback

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.playground.loose.AudioItem
import kotlinx.coroutines.delay

/**
 * Handles all audio-specific playback logic
 */
class AudioPlaybackManager(
    private val player: Player
) {
    companion object {
        private const val TAG = "AudioPlaybackManager"
    }

    /**
     * Play a single audio track
     */
    suspend fun playSingleAudio(
        audio: AudioItem,
        savedPosition: Long = 0L,
        autoPlay: Boolean = true
    ): Boolean {
        return try {
            Log.d(TAG, "=== PLAY AUDIO START ===")
            Log.d(TAG, "Title: ${audio.title}")

            player.stop()
            player.clearMediaItems()

            val mediaItem = buildAudioMediaItem(audio)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = autoPlay

            // Disable repeat mode for single tracks
            player.repeatMode = Player.REPEAT_MODE_OFF

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

            Log.d(TAG, "=== PLAY AUDIO END ===")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error playing audio", e)
            false
        }
    }

    /**
     * Play a playlist of audio tracks
     */
    suspend fun playAudioPlaylist(
        playlist: List<AudioItem>,
        startIndex: Int = 0,
        savedPosition: Long = 0L,
        repeatMode: Int = Player.REPEAT_MODE_OFF
    ): Boolean {
        return try {
            Log.d(TAG, "▶️ Playing audio playlist: ${playlist.size} items, start=$startIndex")

            player.stop()
            player.clearMediaItems()

            val mediaItems = playlist.map { buildAudioMediaItem(it) }
            player.setMediaItems(mediaItems, startIndex, savedPosition.coerceAtLeast(0L))
            player.prepare()
            player.playWhenReady = true
            player.repeatMode = repeatMode

            Log.d(TAG, "✅ Playlist loaded successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error playing audio playlist", e)
            false
        }
    }

    /**
     * Build MediaItem with complete metadata for audio
     */
    private fun buildAudioMediaItem(audio: AudioItem): MediaItem {
        return MediaItem.Builder()
            .setUri(audio.uri)
            .setMediaId(audio.id.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(audio.title)
                    .setArtist(audio.artist ?: "Unknown Artist")
                    .setAlbumTitle(audio.album)
                    .setArtworkUri(audio.albumArtUri)
                    .build()
            )
            .build()
    }

    /**
     * Navigate to next audio in playlist
     */
    fun playNextAudio() {
        when {
            player.hasNextMediaItem() -> {
                player.seekToNextMediaItem()
            }
            player.repeatMode == Player.REPEAT_MODE_ALL && player.mediaItemCount > 0 -> {
                player.seekTo(0, 0)
                player.play()
            }
            player.mediaItemCount > 0 -> {
                // No repeat mode, loop anyway for better UX
                player.seekTo(0, 0)
                player.play()
            }
        }
    }

    /**
     * Navigate to previous audio in playlist
     */
    fun playPreviousAudio() {
        when {
            player.currentPosition > 3000 -> {
                // Restart current track if > 3 seconds
                player.seekTo(0)
            }
            player.hasPreviousMediaItem() -> {
                player.seekToPreviousMediaItem()
            }
            player.repeatMode == Player.REPEAT_MODE_ALL && player.mediaItemCount > 0 -> {
                // Loop to last item
                player.seekTo(player.mediaItemCount - 1, 0)
                player.play()
            }
            player.mediaItemCount > 0 -> {
                // No repeat mode, loop anyway for better UX
                player.seekTo(player.mediaItemCount - 1, 0)
                player.play()
            }
        }
    }

    /**
     * Get current audio item from player
     */
    fun getCurrentAudioId(): Long? {
        return player.currentMediaItem?.mediaId?.toLongOrNull()
    }

    private fun formatTime(ms: Long): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val secs = seconds % 60
        return "${minutes}:${secs.toString().padStart(2, '0')}"
    }
}