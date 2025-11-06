package com.playground.loose

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * ViewModel specifically for Video playback
 * Handles: Video library, video playback, and video-as-audio mode
 */
class VideoPlayerViewModel(
    application: Application,
    private val player: Player,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "VideoPlayerViewModel"
    }

    // CRITICAL FIX: Expose the player instance
    fun getPlayer(): Player = player

    // ============ Managers ============
    private val preferencesManager = PreferencesManager(application)
    private val mediaScanner = MediaScanner(application)
    private val videoManager = VideoPlaybackManager(player)
    private val stateManager = PlaybackStateManager(player, viewModelScope)
    private val queueManager = MediaQueueManager()
    private val videoAsAudioManager = VideoAsAudioManager()
    private val repeatModeManager = RepeatModeManager(player)
    private val speedManager = PlaybackSpeedManager(player)

    // ============ Video Library State ============
    private val _videoItems = MutableStateFlow<List<VideoItem>>(emptyList())
    val videoItems: StateFlow<List<VideoItem>> = _videoItems.asStateFlow()

    private val _currentVideoItem = MutableStateFlow<VideoItem?>(null)
    val currentVideoItem: StateFlow<VideoItem?> = _currentVideoItem.asStateFlow()

    private val _videoPositions = MutableStateFlow<Map<Long, Long>>(emptyMap())
    val videoPositions: StateFlow<Map<Long, Long>> = _videoPositions.asStateFlow()

    private val _recentlyPlayedVideoIds = MutableStateFlow<List<Long>>(emptyList())
    val recentlyPlayedVideoIds: StateFlow<List<Long>> = _recentlyPlayedVideoIds.asStateFlow()

    // ============ UI State ============
    private val _videoViewMode = MutableStateFlow(ViewMode.LIST)
    val videoViewMode: StateFlow<ViewMode> = _videoViewMode.asStateFlow()

    private val _videoSortOption = MutableStateFlow(SortOption.NAME)
    val videoSortOption: StateFlow<SortOption> = _videoSortOption.asStateFlow()

    // ============ Exposed Manager States ============
    val isPlaying: StateFlow<Boolean> = stateManager.isPlaying
    val currentPosition: StateFlow<Long> = stateManager.currentPosition
    val duration: StateFlow<Long> = stateManager.duration
    val currentQueue: StateFlow<List<MediaItemInfo>> = queueManager.currentQueue
    val currentQueueIndex: StateFlow<Int> = queueManager.currentQueueIndex
    val isVideoAsAudioMode: StateFlow<Boolean> = videoAsAudioManager.isVideoAsAudioMode
    val repeatMode: StateFlow<RepeatMode> = repeatModeManager.repeatMode
    val playbackSpeed: StateFlow<Float> = speedManager.playbackSpeed

    // ============ Internal State ============
    private var lastPlayedVideoId = 0L
    private var hasSetupListener = false

    init {
        Log.d(TAG, "🎬 VideoPlayerViewModel initializing...")
        observePreferences()
        loadVideoItems()
    }

    // CRITICAL FIX: Make setupPlayerListener public so it can be called when needed
    fun setupPlayerListener() {
        if (hasSetupListener) {
            Log.d(TAG, "⚠️ Player listener already set up, skipping")
            return
        }

        hasSetupListener = true
        Log.d(TAG, "🎧 Setting up player listener")

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                stateManager.updatePlaybackState(isPlaying, player.duration)
                Log.d(TAG, "🎬 Is playing changed: $isPlaying")

                if (!isPlaying && player.currentPosition > 0) {
                    viewModelScope.launch {
                        delay(100)
                        saveCurrentVideoSession(force = true)
                    }
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    stateManager.updatePlaybackState(player.isPlaying, player.duration)
                    Log.d(TAG, "✅ Player READY - Duration: ${player.duration}ms")
                }

                if (playbackState == Player.STATE_ENDED) {
                    handlePlaybackEnded()
                }
            }
        })
    }

    // ============================================
    // VIDEO PLAYBACK FUNCTIONS
    // ============================================

    fun playVideo(video: VideoItem, autoPlay: Boolean = true) {
        if (lastPlayedVideoId == video.id && _currentVideoItem.value?.id == video.id && !autoPlay) {
            Log.d(TAG, "⏭️ Ignoring duplicate playVideo call")
            return
        }

        lastPlayedVideoId = video.id

        // Ensure listener is set up before playback
        setupPlayerListener()

        viewModelScope.launch {
            val savedPosition = _videoPositions.value[video.id] ?: 0L

            val success = videoManager.playSingleVideo(video, savedPosition, autoPlay)

            if (success) {
                _currentVideoItem.value = video
                videoAsAudioManager.disableVideoAsAudioMode()

                queueManager.buildVideoQueue(_videoItems.value, video)

                // Add to recently played
                preferencesManager.addRecentlyPlayedVideo(video.id)
                _recentlyPlayedVideoIds.value =
                    preferencesManager.appPreferences.first().recentlyPlayedVideos.map { it.videoId }

                sessionManager.saveSession(
                    mediaId = video.id,
                    position = if (savedPosition > 0) savedPosition else 0L,
                    isAudioMode = false,
                    force = true
                )
            }
        }
    }

    // ============================================
    // VIDEO-AS-AUDIO MODE
    // ============================================

    fun playVideoAsAudio() {
        val video = _currentVideoItem.value
        if (video == null) {
            Log.w(TAG, "⚠️ No video playing, cannot switch to audio mode")
            return
        }

        videoAsAudioManager.enableVideoAsAudioMode(video)
        Log.d(TAG, "✅ Switched to audio-only mode")
    }

    fun returnToVideoPlayer() {
        videoAsAudioManager.disableVideoAsAudioMode()
        Log.d(TAG, "🎬 Returning to video player")
    }

    // ============================================
    // PLAYBACK CONTROL
    // ============================================

    fun playPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.playbackState == Player.STATE_IDLE) {
                player.prepare()
            }
            player.play()
        }
    }

    fun playNext() {
        val nextItem = queueManager.getNextItem()
        if (nextItem?.isAudio == false) {
            _videoItems.value.find { it.id == nextItem.id }?.let {
                playVideo(it, autoPlay = true)
            }
        }
    }

    fun playPrevious() {
        val prevItem = queueManager.getPreviousItem()
        if (prevItem?.isAudio == false) {
            _videoItems.value.find { it.id == prevItem.id }?.let {
                playVideo(it, autoPlay = true)
            }
        }
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
        viewModelScope.launch {
            delay(500)
            saveCurrentVideoSession(force = true)
        }
    }

    fun skipForward() {
        val newPosition = (player.currentPosition + 10000).coerceAtMost(player.duration)
        seekTo(newPosition)
    }

    fun skipBackward() {
        val newPosition = (player.currentPosition - 10000).coerceAtLeast(0)
        seekTo(newPosition)
    }

    // ============================================
    // PLAYBACK FEATURES
    // ============================================

    fun toggleRepeatMode() {
        repeatModeManager.toggleRepeatMode()
        viewModelScope.launch {
            preferencesManager.saveRepeatMode(repeatMode.value)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        speedManager.setSpeed(speed)
    }

    // ============================================
    // UI SETTINGS
    // ============================================

    fun setVideoViewMode(mode: ViewMode) {
        _videoViewMode.value = mode
        viewModelScope.launch {
            preferencesManager.saveVideoViewMode(mode)
        }
    }

    fun setVideoSort(sort: SortOption) {
        _videoSortOption.value = sort
        viewModelScope.launch {
            preferencesManager.saveVideoSort(sort)
            loadVideoItems()
        }
    }

    // ============================================
    // INTERNAL HELPERS
    // ============================================

    private fun handlePlaybackEnded() {
        saveCurrentVideoSession(force = true)

        when (repeatMode.value) {
            RepeatMode.ONE -> {
                player.seekTo(0)
                player.play()
            }
            RepeatMode.ALL -> playNext()
            RepeatMode.OFF -> {
                // Session already saved
            }
        }
    }

    private fun observePreferences() {
        preferencesManager.appPreferences
            .onEach { prefs ->
                _videoViewMode.value = prefs.videoViewMode
                _videoSortOption.value = prefs.videoSortOption
                repeatModeManager.setRepeatMode(prefs.lastPlaybackState.repeatMode)
                _recentlyPlayedVideoIds.value = prefs.recentlyPlayedVideos.map { it.videoId }
                _videoPositions.value = prefs.videoPositions
            }
            .launchIn(viewModelScope)
    }

    fun loadVideoItems() {
        viewModelScope.launch {
            val items = mediaScanner.scanVideoFiles(_videoSortOption.value)
            _videoItems.value = items
            Log.d(TAG, "🎬 Loaded ${items.size} video items")
        }
    }

    private fun saveCurrentVideoSession(force: Boolean = false) {
        val currentMediaId = _currentVideoItem.value?.id ?: return
        var currentPos = player.currentPosition

        if (player.playbackState == Player.STATE_ENDED) {
            currentPos = 0L
        }

        viewModelScope.launch {
            sessionManager.saveSession(
                mediaId = currentMediaId,
                position = currentPos,
                isAudioMode = false,
                force = force
            )

            preferencesManager.saveVideoPosition(currentMediaId, currentPos)
        }
    }

    public override fun onCleared() {
        saveCurrentVideoSession(force = true)
        stateManager.cleanup()
        super.onCleared()
        Log.d(TAG, "🛑 VideoPlayerViewModel cleared")
    }
}