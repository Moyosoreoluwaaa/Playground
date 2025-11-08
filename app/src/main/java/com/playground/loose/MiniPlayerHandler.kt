package com.playground.loose

import android.util.Log
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.loose.mediaplayer.ui.viewmodel.PlayerViewModel

/**
 * Handles mini player click logic to ensure displayed media matches playback.
 * Verifies sync between UI state and player state before navigation.
 */
@UnstableApi
object MiniPlayerHandler {
    private const val TAG = "MiniPlayerHandler"

    /**
     * Handle mini player click with proper state verification and sync.
     * Determines which media is ACTUALLY playing and navigates to correct screen.
     *
     * @param viewModel The PlayerViewModel instance
     * @param currentAudio Currently displayed audio item (may be null)
     * @param currentVideo Currently displayed video item (may be null)
     * @param isAudioMode Whether in audio mode
     * @param isVideoAsAudioMode Whether in video-as-audio mode
     * @param player The ExoPlayer instance
     * @param onNavigateToAudioPlayer Callback to navigate to audio player
     * @param onNavigateToVideoPlayer Callback to navigate to video player
     */

    suspend fun handleMiniPlayerClick(
        viewModel: PlayerViewModel,
        currentAudio: AudioItem?,
        currentVideo: VideoItem?,
        isAudioMode: Boolean,
        isVideoAsAudioMode: Boolean,
        player: Player,
        onNavigateToAudioPlayer: () -> Unit,
        onNavigateToVideoPlayer: () -> Unit
    ) {
        Log.d(TAG, "=== MINI PLAYER CLICK ===")
        Log.d(TAG, "isAudioMode: $isAudioMode")
        Log.d(TAG, "isVideoAsAudioMode: $isVideoAsAudioMode")
        Log.d(TAG, "currentAudio: ${currentAudio?.title}")
        Log.d(TAG, "currentVideo: ${currentVideo?.title}")

        val playerMediaId = player.currentMediaItem?.mediaId?.toLongOrNull()
        Log.d(TAG, "Player mediaId: $playerMediaId")

        // CRITICAL: Determine which media is ACTUALLY playing by matching player's mediaId
        val actuallyPlayingAudio = currentAudio?.id == playerMediaId
        val actuallyPlayingVideo = currentVideo?.id == playerMediaId

        Log.d(TAG, "Actually playing audio: $actuallyPlayingAudio")
        Log.d(TAG, "Actually playing video: $actuallyPlayingVideo")

        when {
            // Case 1: Video playing as audio (special mode)
            isVideoAsAudioMode && actuallyPlayingVideo && currentVideo != null -> {
                Log.d(TAG, "→ Case: Video-as-Audio Mode (player has video)")
                handleVideoAsAudioClick(
                    viewModel = viewModel,
                    currentVideo = currentVideo,
                    playerMediaId = playerMediaId,
                    player = player,
                    onNavigate = onNavigateToAudioPlayer
                )
            }

            // Case 2: Audio is actually playing
            actuallyPlayingAudio && currentAudio != null -> {
                Log.d(TAG, "→ Case: Audio is playing (navigate to audio player)")
                handleAudioClick(
                    viewModel = viewModel,
                    currentAudio = currentAudio,
                    playerMediaId = playerMediaId,
                    player = player,
                    onNavigate = onNavigateToAudioPlayer
                )
            }

            // Case 3: Video is actually playing
            actuallyPlayingVideo && currentVideo != null -> {
                Log.d(TAG, "→ Case: Video is playing (navigate to video player)")
                handleVideoClick(
                    viewModel = viewModel,
                    currentVideo = currentVideo,
                    playerMediaId = playerMediaId,
                    player = player,
                    onNavigate = onNavigateToVideoPlayer
                )
            }

            // Case 4: Mode says audio, but player has different media
            isAudioMode && !isVideoAsAudioMode && currentAudio != null -> {
                Log.d(TAG, "→ Case: Audio mode but out of sync")
                handleAudioClick(
                    viewModel = viewModel,
                    currentAudio = currentAudio,
                    playerMediaId = playerMediaId,
                    player = player,
                    onNavigate = onNavigateToAudioPlayer
                )
            }

            // Case 5: Mode says video
            !isAudioMode && currentVideo != null -> {
                Log.d(TAG, "→ Case: Video mode but out of sync")
                handleVideoClick(
                    viewModel = viewModel,
                    currentVideo = currentVideo,
                    playerMediaId = playerMediaId,
                    player = player,
                    onNavigate = onNavigateToVideoPlayer
                )
            }

            // Case 6: Video as audio but player doesn't have video loaded
            isVideoAsAudioMode && currentVideo != null -> {
                Log.d(TAG, "→ Case: Video-as-audio mode but need to reload video")
                handleVideoAsAudioClick(
                    viewModel = viewModel,
                    currentVideo = currentVideo,
                    playerMediaId = playerMediaId,
                    player = player,
                    onNavigate = onNavigateToAudioPlayer
                )
            }

            else -> {
                Log.w(TAG, "⚠️ No valid media to play")
            }
        }
    }

    private suspend fun handleAudioClick(
        viewModel: PlayerViewModel,
        currentAudio: AudioItem,
        playerMediaId: Long?,
        player: Player,
        onNavigate: () -> Unit
    ) {
        if (playerMediaId == currentAudio.id) {
            Log.d(TAG, "✅ Audio in sync, navigating directly")
            onNavigate()
        } else {
            Log.d(TAG, "🔄 Audio out of sync, reloading: ${currentAudio.title}")
            val currentPosition = player.currentPosition
            val wasPlaying = player.isPlaying

            // Reload the correct audio
            viewModel.playAudio(currentAudio, autoPlay = wasPlaying)

            // Wait for player to be ready
            var attempts = 0
            while (player.playbackState != Player.STATE_READY && attempts < 20) {
                kotlinx.coroutines.delay(100)
                attempts++
            }

            // Restore position if same media (just out of sync)
            if (currentPosition > 0 && player.duration > 0) {
                player.seekTo(currentPosition.coerceIn(0, player.duration))
            }

            Log.d(TAG, "✅ Audio reloaded, navigating")
            onNavigate()
        }
    }

    private suspend fun handleVideoClick(
        viewModel: PlayerViewModel,
        currentVideo: VideoItem,
        playerMediaId: Long?,
        player: Player,
        onNavigate: () -> Unit
    ) {
        if (playerMediaId == currentVideo.id) {
            Log.d(TAG, "✅ Video in sync, navigating directly")
            onNavigate()
        } else {
            Log.d(TAG, "🔄 Video out of sync, reloading: ${currentVideo.title}")
            val currentPosition = player.currentPosition
            val wasPlaying = player.isPlaying

            // Reload the correct video
            viewModel.playVideo(currentVideo, autoPlay = wasPlaying)

            // Wait for player to be ready
            var attempts = 0
            while (player.playbackState != Player.STATE_READY && attempts < 20) {
                kotlinx.coroutines.delay(100)
                attempts++
            }

            // Restore position if same media (just out of sync)
            if (currentPosition > 0 && player.duration > 0) {
                player.seekTo(currentPosition.coerceIn(0, player.duration))
            }

            Log.d(TAG, "✅ Video reloaded, navigating")
            onNavigate()
        }
    }

    private suspend fun handleVideoAsAudioClick(
        viewModel: PlayerViewModel,
        currentVideo: VideoItem,
        playerMediaId: Long?,
        player: Player,
        onNavigate: () -> Unit
    ) {
        if (playerMediaId == currentVideo.id) {
            Log.d(TAG, "✅ Video-as-audio in sync, navigating to audio player")
            onNavigate()
        } else {
            Log.d(TAG, "🔄 Video-as-audio out of sync, reloading: ${currentVideo.title}")
            val currentPosition = player.currentPosition
            val wasPlaying = player.isPlaying

            // Reload video then switch to audio mode
            viewModel.playVideo(currentVideo, autoPlay = wasPlaying)

            // Wait for player to be ready
            var attempts = 0
            while (player.playbackState != Player.STATE_READY && attempts < 20) {
                kotlinx.coroutines.delay(100)
                attempts++
            }

            // Switch to audio-only mode
            viewModel.playVideoAsAudio()

            // Restore position
            if (currentPosition > 0 && player.duration > 0) {
                player.seekTo(currentPosition.coerceIn(0, player.duration))
            }

            Log.d(TAG, "✅ Video-as-audio reloaded, navigating to audio player")
            onNavigate()
        }
    }
}