package com.playground.loose

import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared ViewModel for common media functionality
 * Manages: Player connection, mode switching, and cross-cutting concerns
 */
@UnstableApi
class SharedMediaViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "SharedMediaViewModel"
    }

    // ============ Player Setup ============
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private var isControllerReady = false

    private val fallbackPlayer: Player by lazy { createFallbackPlayer() }
    
    val player: Player get() = controller ?: fallbackPlayer

    // ============ Mode State ============
    private val _isAudioMode = MutableStateFlow(true)
    val isAudioMode: StateFlow<Boolean> = _isAudioMode.asStateFlow()

    // ============ Session Manager ============
    val sessionManager = SessionManager(application)

    // ============ Child ViewModels ============
    private var _audioViewModel: AudioPlayerViewModel? = null
    private var _videoViewModel: VideoPlayerViewModel? = null

    val audioViewModel: AudioPlayerViewModel
        get() {
            if (_audioViewModel == null) {
                _audioViewModel = AudioPlayerViewModel(getApplication(), player, sessionManager)
            }
            return _audioViewModel!!
        }

    val videoViewModel: VideoPlayerViewModel
        get() {
            if (_videoViewModel == null) {
                _videoViewModel = VideoPlayerViewModel(getApplication(), player, sessionManager)
            }
            return _videoViewModel!!
        }

    init {
        Log.d(TAG, "🚀 SharedMediaViewModel initializing...")
        initializeMediaController()
    }

    // ============================================
    // MODE SWITCHING
    // ============================================

    /**
     * Switch to audio mode
     */
    fun switchToAudioMode() {
        _isAudioMode.value = true
        Log.d(TAG, "🎵 Switched to audio mode")
    }

    /**
     * Switch to video mode
     */
    fun switchToVideoMode() {
        _isAudioMode.value = false
        Log.d(TAG, "🎬 Switched to video mode")
    }

    /**
     * Check if currently in audio mode
     */
    fun isInAudioMode(): Boolean = _isAudioMode.value

    /**
     * Check if currently in video mode
     */
    fun isInVideoMode(): Boolean = !_isAudioMode.value

    // ============================================
    // PLAYER INITIALIZATION
    // ============================================

    private fun initializeMediaController() {
        val sessionToken = SessionToken(
            getApplication(),
            ComponentName(getApplication(), MediaPlaybackService::class.java)
        )

        controllerFuture = MediaController.Builder(getApplication(), sessionToken)
            .buildAsync()

        controllerFuture?.addListener(
            {
                try {
                    controller = controllerFuture?.get()
                    isControllerReady = true
                    Log.d(TAG, "✅ MediaController connected")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Failed to connect MediaController", e)
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun createFallbackPlayer(): Player {
        Log.w(TAG, "🎮️ Creating FALLBACK player")
        return ExoPlayer.Builder(getApplication())
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    // ============================================
    // SESSION RESTORATION
    // ============================================

    suspend fun restoreLastSession() {
        val savedSession = sessionManager.getSession()
        if (savedSession == null) {
            Log.d(TAG, "ℹ️ No session to restore")
            return
        }

        Log.d(TAG, "🔄 Restoring session: mediaId=${savedSession.mediaId}, isAudio=${savedSession.isAudioMode}")

        // Wait for controller
        var attempts = 0
        while (!isControllerReady && attempts < 50) {
            delay(100)
            attempts++
        }

        if (savedSession.isAudioMode) {
            _isAudioMode.value = true
            // Delegate to audio ViewModel
            // audioViewModel will handle restoration
        } else {
            _isAudioMode.value = false
            // Delegate to video ViewModel
            // videoViewModel will handle restoration
        }
    }

    // ============================================
    // CLEANUP
    // ============================================

    override fun onCleared() {
        // Clean up child ViewModels
        _audioViewModel?.onCleared()
        _videoViewModel?.onCleared()

        // Release player
        if (fallbackPlayer.playbackState != Player.STATE_IDLE) {
            (fallbackPlayer as? ExoPlayer)?.release()
        }
        controllerFuture?.let { MediaController.releaseFuture(it) }

        super.onCleared()
        Log.d(TAG, "🛑 SharedMediaViewModel cleared")
    }

    // ============================================
    // UTILITY
    // ============================================

    /**
     * Get current playing media info
     */
    fun getCurrentMediaInfo(): Pair<Long?, Boolean> {
        return if (_isAudioMode.value) {
            audioViewModel.currentAudioItem.value?.id to true
        } else {
            videoViewModel.currentVideoItem.value?.id to false
        }
    }

    /**
     * Check if any media is playing
     */
    fun isPlayingAnyMedia(): Boolean {
        return player.isPlaying
    }
}