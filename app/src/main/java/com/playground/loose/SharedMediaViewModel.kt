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
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

/**
 * Shared ViewModel for common media functionality
 * Manages: Player connection, context switching, and cross-cutting concerns
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

    // ============ Context State (CRITICAL NEW FEATURE) ============
    private val _currentContext = MutableStateFlow(PlaybackContext.NONE)
    val currentContext: StateFlow<PlaybackContext> = _currentContext.asStateFlow()

    // Keep old isAudioMode for backwards compatibility during transition
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
                _audioViewModel = AudioPlayerViewModel(getApplication(), player, sessionManager, this)
            }
            return _audioViewModel!!
        }

    val videoViewModel: VideoPlayerViewModel
        get() {
            if (_videoViewModel == null) {
                _videoViewModel = VideoPlayerViewModel(getApplication(), player, sessionManager, this)
            }
            return _videoViewModel!!
        }

    init {
        Log.d(TAG, "🚀 SharedMediaViewModel initializing...")
        initializeMediaController()
    }

    // ============================================
    // CONTEXT SWITCHING (THE FIX!)
    // ============================================

    /**
     * Switch to pure audio playback context
     * - Stops video playback
     * - Clears video state
     * - Disables video-as-audio mode
     */
    fun switchToAudioContext() {
        if (_currentContext.value == PlaybackContext.AUDIO) {
            Log.d(TAG, "⏭️ Already in AUDIO context, skipping")
            return
        }

        Log.d(TAG, "🎵 Switching to AUDIO context (from ${_currentContext.value})")

        // Clear video state
        if (_currentContext.value.isVideoContext()) {
            _videoViewModel?.clearStateForContextSwitch()
        }

        _currentContext.value = PlaybackContext.AUDIO
        _isAudioMode.value = true
    }

    /**
     * Switch to visual video playback context
     * - Stops audio playback
     * - Clears audio state
     * - Disables video-as-audio mode
     */
    fun switchToVideoVisualContext() {
        if (_currentContext.value == PlaybackContext.VIDEO_VISUAL) {
            Log.d(TAG, "⏭️ Already in VIDEO_VISUAL context, skipping")
            return
        }

        Log.d(TAG, "🎬 Switching to VIDEO_VISUAL context (from ${_currentContext.value})")

        // Clear audio state
        if (_currentContext.value == PlaybackContext.AUDIO) {
            _audioViewModel?.clearStateForContextSwitch()
        }

        // Disable video-as-audio mode if coming from it
        if (_currentContext.value == PlaybackContext.VIDEO_AUDIO_ONLY) {
            _videoViewModel?.returnToVideoPlayer()
        }

        _currentContext.value = PlaybackContext.VIDEO_VISUAL
        _isAudioMode.value = false
    }

    /**
     * Switch to video-as-audio playback context
     * - Stops audio playback
     * - Clears audio state
     * - Keeps video playing but changes UI
     */
    fun switchToVideoAsAudioContext() {
        if (_currentContext.value == PlaybackContext.VIDEO_AUDIO_ONLY) {
            Log.d(TAG, "⏭️ Already in VIDEO_AUDIO_ONLY context, skipping")
            return
        }

        Log.d(TAG, "🎧 Switching to VIDEO_AUDIO_ONLY context (from ${_currentContext.value})")

        // Clear audio state
        if (_currentContext.value == PlaybackContext.AUDIO) {
            _audioViewModel?.clearStateForContextSwitch()
        }

        _currentContext.value = PlaybackContext.VIDEO_AUDIO_ONLY
        _isAudioMode.value = true // Audio-style UI
    }

    /**
     * Get the current context
     */
    fun getCurrentContext(): PlaybackContext = _currentContext.value

    /**
     * Check if currently in audio context
     */
    fun isInAudioContext(): Boolean = _currentContext.value == PlaybackContext.AUDIO

    /**
     * Check if currently in any video context
     */
    fun isInVideoContext(): Boolean = _currentContext.value.isVideoContext()

    // Old methods kept for backwards compatibility
    fun switchToAudioMode() = switchToAudioContext()
    fun switchToVideoMode() = switchToVideoVisualContext()
    fun isInAudioMode(): Boolean = _isAudioMode.value
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
            _currentContext.value = PlaybackContext.NONE
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
            _currentContext.value = PlaybackContext.AUDIO
            _isAudioMode.value = true
        } else {
            _currentContext.value = PlaybackContext.VIDEO_VISUAL
            _isAudioMode.value = false
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
     * Get current playing media info with context
     */
    fun getCurrentMediaInfo(): Triple<Long?, Boolean, PlaybackContext> {
        return when (_currentContext.value) {
            PlaybackContext.AUDIO -> Triple(
                audioViewModel.currentAudioItem.value?.id,
                true,
                PlaybackContext.AUDIO
            )
            PlaybackContext.VIDEO_VISUAL, PlaybackContext.VIDEO_AUDIO_ONLY -> Triple(
                videoViewModel.currentVideoItem.value?.id,
                false,
                _currentContext.value
            )
            PlaybackContext.NONE -> Triple(null, true, PlaybackContext.NONE)
        }
    }

    /**
     * Check if any media is playing
     */
    fun isPlayingAnyMedia(): Boolean {
        return player.isPlaying
    }

    /**
     * Get appropriate play/pause handler for current context
     */
    fun playPause() {
        when (_currentContext.value) {
            PlaybackContext.AUDIO -> audioViewModel.playPause()
            PlaybackContext.VIDEO_VISUAL, PlaybackContext.VIDEO_AUDIO_ONLY -> videoViewModel.playPause()
            PlaybackContext.NONE -> Log.w(TAG, "⚠️ No context active for playPause")
        }
    }

    /**
     * Get appropriate next handler for current context
     */
    fun playNext() {
        when (_currentContext.value) {
            PlaybackContext.AUDIO -> audioViewModel.playNext()
            PlaybackContext.VIDEO_VISUAL, PlaybackContext.VIDEO_AUDIO_ONLY -> videoViewModel.playNext()
            PlaybackContext.NONE -> Log.w(TAG, "⚠️ No context active for playNext")
        }
    }
}