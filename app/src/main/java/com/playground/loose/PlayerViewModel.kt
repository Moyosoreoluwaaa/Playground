package com.loose.mediaplayer.ui.viewmodel

import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer // Import ExoPlayer for casting
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.playground.loose.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@UnstableApi
class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val mediaScanner = MediaScanner(application)
    private val sessionManager = SessionManager(application)

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private var isControllerReady = false

    private val fallbackPlayer: Player by lazy {
        createFallbackPlayer()
    }

    val player: Player
        get() = controller ?: fallbackPlayer

    // State flows
    private val _audioItems = MutableStateFlow<List<AudioItem>>(emptyList())
    val audioItems: StateFlow<List<AudioItem>> = _audioItems.asStateFlow()

    private val _videoItems = MutableStateFlow<List<VideoItem>>(emptyList())
    val videoItems: StateFlow<List<VideoItem>> = _videoItems.asStateFlow()

    private val _currentAudioItem = MutableStateFlow<AudioItem?>(null)
    val currentAudioItem: StateFlow<AudioItem?> = _currentAudioItem.asStateFlow()

    private val _currentVideoItem = MutableStateFlow<VideoItem?>(null)
    val currentVideoItem: StateFlow<VideoItem?> = _currentVideoItem.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _isAudioMode = MutableStateFlow(true)
    val isAudioMode: StateFlow<Boolean> = _isAudioMode.asStateFlow()

    private val _audioViewMode = MutableStateFlow(ViewMode.LIST)
    val audioViewMode: StateFlow<ViewMode> = _audioViewMode.asStateFlow()

    private val _videoViewMode = MutableStateFlow(ViewMode.LIST)
    val videoViewMode: StateFlow<ViewMode> = _videoViewMode.asStateFlow()

    private val _audioSortOption = MutableStateFlow(SortOption.NAME)
    val audioSortOption: StateFlow<SortOption> = _audioSortOption.asStateFlow()

    private val _videoSortOption = MutableStateFlow(SortOption.NAME)
    val videoSortOption: StateFlow<SortOption> = _videoSortOption.asStateFlow()

    private val _recentlyPlayedVideoIds = MutableStateFlow<List<Long>>(emptyList())
    val recentlyPlayedVideoIds: StateFlow<List<Long>> = _recentlyPlayedVideoIds.asStateFlow()

    private val _audioPositions = MutableStateFlow<Map<Long, Long>>(emptyMap())
    val audioPositions: StateFlow<Map<Long, Long>> = _audioPositions.asStateFlow()

    private val _videoPositions = MutableStateFlow<Map<Long, Long>>(emptyMap())
    val videoPositions: StateFlow<Map<Long, Long>> = _videoPositions.asStateFlow()

    // NEW: A-B Loop state
    private val _abLoopState = MutableStateFlow(ABLoopState())
    val abLoopState: StateFlow<ABLoopState> = _abLoopState.asStateFlow()

    // NEW: Sleep timer state
    private val _sleepTimerRemaining = MutableStateFlow(0L)
    val sleepTimerRemaining: StateFlow<Long> = _sleepTimerRemaining.asStateFlow()
    private var sleepTimerJob: Job? = null

    // NEW: Queue state for proper next/previous
    private val _currentQueue = MutableStateFlow<List<MediaItemInfo>>(emptyList())
    val currentQueue: StateFlow<List<MediaItemInfo>> = _currentQueue.asStateFlow()

    private val _currentQueueIndex = MutableStateFlow(0)
    val currentQueueIndex: StateFlow<Int> = _currentQueueIndex.asStateFlow()

    private val _audioPlaylists = MutableStateFlow<List<AudioPlaylist>>(emptyList())
    val audioPlaylists: StateFlow<List<AudioPlaylist>> = _audioPlaylists.asStateFlow()

    private var currentPlaylist = listOf<Long>()
    private var currentIndex = 0

    private var positionTrackingJob: Job? = null
    private var hasRestoredSession = false
    private var isLoadingMedia = false
    private var lastPlayedMediaId = 0L

    // PlayerViewModel.kt - Add new state for playlist mode
    private val _isPlaylistMode = MutableStateFlow(false)
    val isPlaylistMode: StateFlow<Boolean> = _isPlaylistMode.asStateFlow()

    private var playlistQueue = listOf<Long>()

    // PlayerViewModel.kt - Add playbackSpeed state
    private val _playbackSpeed = MutableStateFlow(1f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()


    companion object {
        private const val TAG = "PlayerViewModel"
    }

    init {
        Log.d(TAG, "🚀 PlayerViewModel initializing...")
        observePreferences()
        preferencesManager.audioPlaylists
            .onEach { playlists ->
                _audioPlaylists.value = playlists
            }
            .launchIn(viewModelScope)

        loadMediaAndRestore()
    }

    // Update playSelectedAudio function
    fun playSelectedAudio(selection: List<AudioItem>, startIndex: Int = 0) {
        if (selection.isEmpty()) return

        viewModelScope.launch {
            val startAudio = selection[startIndex]
            val savedPosition = _audioPositions.value[startAudio.id] ?: 0L

            try {
                player.stop()
                player.clearMediaItems()

                val mediaItems = selection.map { it.toMediaItem() }
                player.setMediaItems(mediaItems, startIndex, savedPosition.coerceAtLeast(0L))
                player.prepare()
                player.playWhenReady = true

                _currentAudioItem.value = startAudio
                _isAudioMode.value = true

                // Mark as playlist mode
                _isPlaylistMode.value = true
                playlistQueue = selection.map { it.id }

                // Set repeat mode on player
                player.repeatMode = when (_repeatMode.value) {
                    RepeatMode.OFF -> Player.REPEAT_MODE_OFF
                    RepeatMode.ONE -> Player.REPEAT_MODE_ONE
                    RepeatMode.ALL -> Player.REPEAT_MODE_ALL
                }

                // BUILD queue from selection
                val queue = selection.map { MediaItemInfo(it.id, it.title, true) }
                _currentQueue.value = queue
                _currentQueueIndex.value = startIndex
                currentPlaylist = selection.map { it.id }
                currentIndex = startIndex

                Log.d(TAG, "✅ Playlist mode: ${selection.size} items, repeat=${_repeatMode.value}")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error playing selected audio", e)
            }
        }
    }

    // Update playAudio to disable playlist mode
    fun playAudio(audio: AudioItem, autoPlay: Boolean = true) {
        if (lastPlayedMediaId == audio.id && _currentAudioItem.value?.id == audio.id && !autoPlay) {
            Log.d(TAG, "⏭️ Ignoring duplicate playAudio call")
            return
        }

        lastPlayedMediaId = audio.id
        Log.d(TAG, "=== PLAY AUDIO START ===")
        Log.d(TAG, "Title: ${audio.title}")

        viewModelScope.launch {
            val savedPosition = _audioPositions.value[audio.id] ?: 0L
            if (savedPosition > 1000) {
                Log.d(TAG, "🔄 Found saved position: ${formatTime(savedPosition)}")
            }

            try {
                player.stop()
                player.clearMediaItems()

                val mediaItem = audio.toMediaItem()

                player.setMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = autoPlay

                _currentAudioItem.value = audio
                _isAudioMode.value = true

                // Disable playlist mode
                _isPlaylistMode.value = false
                playlistQueue = emptyList()

                // BUILD proper queue from all audio
                buildQueueFromAllAudio(audio)

                // Set repeat mode OFF when playing single track
                player.repeatMode = Player.REPEAT_MODE_OFF

                var attempts = 0
                while (player.playbackState != Player.STATE_READY && attempts < 20) {
                    delay(100)
                    attempts++
                }

                if (player.playbackState == Player.STATE_READY) {
                    if (savedPosition > 0) {
                        val validPosition = savedPosition.coerceIn(0, player.duration)
                        player.seekTo(validPosition)
                        Log.d(TAG, "⏩ Seeking to saved position: ${formatTime(validPosition)}")
                    }

                    sessionManager.saveSession(
                        mediaId = audio.id,
                        position = if (savedPosition > 0) savedPosition else 0L,
                        isAudioMode = true,
                        force = true
                    )
                }

                Log.d(TAG, "=== PLAY AUDIO END ===")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error playing audio", e)
            }
        }
    }

    // Update toggleRepeatMode to sync with player
    fun toggleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }

        // Sync player repeat mode
        player.repeatMode = when (_repeatMode.value) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
        }

        viewModelScope.launch {
            preferencesManager.saveRepeatMode(_repeatMode.value)
        }

        Log.d(TAG, "🔁 Repeat mode: ${_repeatMode.value}, playlist mode: ${_isPlaylistMode.value}")
    }

    // Update playNext to respect playlist mode
    fun playNext() {
        // In playlist mode, let ExoPlayer handle navigation
        if (_isPlaylistMode.value && player.mediaItemCount > 1) {
            if (player.hasNextMediaItem()) {
                player.seekToNextMediaItem()
                updateCurrentItemFromPlayer()
            } else if (player.repeatMode == Player.REPEAT_MODE_ALL) {
                player.seekTo(0, 0)
                updateCurrentItemFromPlayer()
            }
            return
        }

        // Normal mode - use queue
        if (_currentQueue.value.isEmpty()) {
            Log.w(TAG, "⚠️ Queue is empty, cannot play next")
            return
        }

        val nextIndex = (_currentQueueIndex.value + 1) % _currentQueue.value.size
        val nextItem = _currentQueue.value[nextIndex]

        Log.d(TAG, "⏭️ Playing next: ${nextItem.title} (index $nextIndex)")

        if (nextItem.isAudio) {
            _audioItems.value.find { it.id == nextItem.id }?.let {
                playAudio(it, autoPlay = true)
            }
        } else {
            _videoItems.value.find { it.id == nextItem.id }?.let {
                playVideo(it, autoPlay = true)
            }
        }
    }

    // Update playPrevious to respect playlist mode
    fun playPrevious() {
        // In playlist mode, let ExoPlayer handle navigation
        if (_isPlaylistMode.value && player.mediaItemCount > 1) {
            if (player.currentPosition > 3000) {
                player.seekTo(0)
            } else {
                if (player.hasPreviousMediaItem()) {
                    player.seekToPreviousMediaItem()
                    updateCurrentItemFromPlayer()
                } else if (player.repeatMode == Player.REPEAT_MODE_ALL) {
                    player.seekTo(player.mediaItemCount - 1, 0)
                    updateCurrentItemFromPlayer()
                }
            }
            return
        }

        // Normal mode - use queue
        if (_currentQueue.value.isEmpty()) {
            Log.w(TAG, "⚠️ Queue is empty, cannot play previous")
            return
        }

        val prevIndex = if (_currentQueueIndex.value - 1 < 0) {
            _currentQueue.value.size - 1
        } else {
            _currentQueueIndex.value - 1
        }
        val prevItem = _currentQueue.value[prevIndex]

        Log.d(TAG, "⏮️ Playing previous: ${prevItem.title} (index $prevIndex)")

        if (prevItem.isAudio) {
            _audioItems.value.find { it.id == prevItem.id }?.let {
                playAudio(it, autoPlay = true)
            }
        } else {
            _videoItems.value.find { it.id == prevItem.id }?.let {
                playVideo(it, autoPlay = true)
            }
        }
    }

    // Add helper function to update current item from player
    private fun updateCurrentItemFromPlayer() {
        val currentItem = player.currentMediaItem
        val mediaId = currentItem?.mediaId?.toLongOrNull() ?: return

        if (_isAudioMode.value) {
            _audioItems.value.find { it.id == mediaId }?.let {
                _currentAudioItem.value = it
                _currentQueueIndex.value = playlistQueue.indexOf(mediaId).coerceAtLeast(0)
            }
        }
    }

    private fun observePreferences() {
        preferencesManager.appPreferences
            .onEach { prefs ->
                Log.d(TAG, "📝 Preferences updated")
                _audioViewMode.value = prefs.audioViewMode
                _videoViewMode.value = prefs.videoViewMode
                _audioSortOption.value = prefs.audioSortOption
                _videoSortOption.value = prefs.videoSortOption
                _repeatMode.value = prefs.lastPlaybackState.repeatMode
                _recentlyPlayedVideoIds.value = prefs.recentlyPlayedVideos.map { it.videoId }
                _audioPositions.value = prefs.audioPositions
                _videoPositions.value = prefs.videoPositions
            }
            .launchIn(viewModelScope)
    }

    private fun AudioItem.toMediaItem(): MediaItem = MediaItem.Builder()
        .setUri(this.uri)
        .setMediaId(this.id.toString())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(this.title)
                .setArtist(this.artist)
                .setAlbumTitle(this.album)
                .setArtworkUri(this.albumArtUri)
                .build()
        )
        .build()

    private fun VideoItem.toMediaItem(): MediaItem = MediaItem.Builder()
        .setUri(this.uri)
        .setMediaId(this.id.toString())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(this.title)
                .build()
        )
        .build()

    private fun loadMediaAndRestore() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "📋 Waiting for initial preferences...")
                preferencesManager.appPreferences.first()
                Log.d(TAG, "✅ Initial preferences loaded")

                val savedSession = sessionManager.getSession()
                Log.d(TAG, "📝 Saved session: ${savedSession?.let { "mediaId=${it.mediaId}, pos=${formatTime(it.position)}" } ?: "none"}")

                Log.d(TAG, "🎵 Initializing MediaController...")
                initializeMediaController()

                Log.d(TAG, "📚 Loading media items...")
                isLoadingMedia = true
                loadAudioItems()
                loadVideoItems()

                var attempts = 0
                while (attempts < 25 && (_audioItems.value.isEmpty() || _videoItems.value.isEmpty())) {
                    delay(200)
                    attempts++
                }
                isLoadingMedia = false
                Log.d(TAG, "✅ Media items loaded: ${_audioItems.value.size} audio, ${_videoItems.value.size} video")

                attempts = 0
                while (!isControllerReady && attempts < 50) {
                    delay(100)
                    attempts++
                }

                if (!isControllerReady) {
                    Log.e(TAG, "⚠️ MediaController not ready after timeout")
                    if (controller == null) {
                        Log.w(TAG, "⚠️ MediaController timed out. Setting up listener on fallback player.")
                        setupPlayerListener()
                    }
                }

                Log.d(TAG, "✅ MediaController ready")

                if (savedSession != null && savedSession.mediaId != 0L) {
                    Log.d(TAG, "🎬 Restoring session for mediaId=${savedSession.mediaId}")
                    restoreSessionInternal(savedSession)
                } else {
                    Log.d(TAG, "ℹ️ No session to restore")
                    hasRestoredSession = true
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error in loadMediaAndRestore", e)
            }
        }
    }

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
                    setupPlayerListener()
                    controller?.attachDebugger()
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Failed to connect MediaController", e)
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun createFallbackPlayer(): Player {
        Log.w(TAG, "🎚️ Creating FALLBACK player. Controller is not ready. Playback will be local only.")
        return androidx.media3.exoplayer.ExoPlayer.Builder(getApplication())
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

    private fun setupPlayerListener() {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                Log.d(TAG, "🎵 Is playing changed: $isPlaying")

                if (isPlaying) {
                    startPositionTracking()
                } else {
                    stopPositionTracking()
                    if (player.currentPosition > 0) {
                        viewModelScope.launch {
                            delay(100)
                            saveCurrentSession(force = true)
                        }
                    }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.let {
                    _duration.value = player.duration
                    Log.d(TAG, "🎬 Media transition: ${it.mediaMetadata.title}")

                    // UPDATE: Update current queue index when media transitions
                    updateCurrentQueueIndex()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _duration.value = player.duration
                    Log.d(TAG, "✅ Player READY - Duration: ${_duration.value}ms")
                }

                if (playbackState == Player.STATE_ENDED) {
                    handlePlaybackEnded()
                }
            }
        })
    }

    private fun startPositionTracking() {
        positionTrackingJob?.cancel()
        positionTrackingJob = viewModelScope.launch {
            while (true) {
                if (player.isPlaying) {
                    val pos = player.currentPosition
                    _currentPosition.value = pos

                    // CHECK: A-B Loop monitoring
                    checkABLoop(pos)
                }
                delay(500)
            }
        }
    }

    private fun stopPositionTracking() {
        positionTrackingJob?.cancel()
        positionTrackingJob = null
    }

    private fun handlePlaybackEnded() {
        saveCurrentSession(force = true)

        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                player.seekTo(0)
                player.play()
            }
            RepeatMode.ALL -> playNext()
            RepeatMode.OFF -> {
                // Already saved session
            }
        }
    }

    // NEW: A-B Loop check
    private fun checkABLoop(currentPos: Long) {
        val loop = _abLoopState.value
        if (loop.isActive && loop.pointA != null && loop.pointB != null) {
            if (currentPos >= loop.pointB) {
                player.seekTo(loop.pointA)
            }
        }
    }

    // NEW: Set A-B Loop points
    fun setABLoopPointA() {
        val pos = player.currentPosition
        _abLoopState.value = _abLoopState.value.copy(pointA = pos)
        Log.d(TAG, "🔁 A-B Loop Point A set at ${formatTime(pos)}")
    }

    fun setABLoopPointB() {
        val pos = player.currentPosition
        val pointA = _abLoopState.value.pointA
        if (pointA != null && pos > pointA) {
            _abLoopState.value = _abLoopState.value.copy(pointB = pos, isActive = true)
            Log.d(TAG, "🔁 A-B Loop Point B set at ${formatTime(pos)}, loop activated")
        }
    }

    fun clearABLoop() {
        _abLoopState.value = ABLoopState()
        Log.d(TAG, "🔁 A-B Loop cleared")
    }

    // NEW: Sleep Timer functions
    fun startSleepTimer(durationMs: Long) {
        sleepTimerJob?.cancel()
        _sleepTimerRemaining.value = durationMs

        sleepTimerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + durationMs

            while (System.currentTimeMillis() < endTime) {
                _sleepTimerRemaining.value = endTime - System.currentTimeMillis()
                delay(1000)
            }

            // Time's up - pause playback
            player.pause()
            _sleepTimerRemaining.value = 0L
            Log.d(TAG, "💤 Sleep timer ended, playback paused")
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        _sleepTimerRemaining.value = 0L
        Log.d(TAG, "💤 Sleep timer cancelled")
    }

    private suspend fun restoreSessionInternal(session: SessionManager.SavedSession) {
        if (hasRestoredSession) {
            Log.d(TAG, "⚠️ Session already restored, skipping")
            return
        }

        hasRestoredSession = true

        try {
            Log.d(TAG, "🔄 Restoring session...")
            Log.d(TAG, "   MediaID: ${session.mediaId}")
            Log.d(TAG, "   Position: ${formatTime(session.position)}")
            Log.d(TAG, "   IsAudio: ${session.isAudioMode}")

            if (session.isAudioMode) {
                val audio = _audioItems.value.find { it.id == session.mediaId }
                if (audio != null) {
                    Log.d(TAG, "✅ Found audio: ${audio.title}")
                    restoreAudio(audio, session.position)
                } else {
                    Log.w(TAG, "⚠️ Audio item ${session.mediaId} not found")
                }
            } else {
                val video = _videoItems.value.find { it.id == session.mediaId }
                if (video != null) {
                    Log.d(TAG, "✅ Found video: ${video.title}")
                    restoreVideo(video, session.position)
                } else {
                    Log.w(TAG, "⚠️ Video item ${session.mediaId} not found")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to restore session", e)
        }
    }

    private suspend fun restoreAudio(audio: AudioItem, position: Long) {
        try {
            Log.d(TAG, "🎵 Restoring audio: ${audio.title} at ${formatTime(position)}")

            player.stop()
            player.clearMediaItems()

            val mediaItem = audio.toMediaItem()

            player.setMediaItem(mediaItem)
            player.prepare()

            var waitAttempts = 0
            while (player.playbackState != Player.STATE_READY && waitAttempts < 30) {
                delay(100)
                waitAttempts++
            }

            if (player.playbackState == Player.STATE_READY) {
                val validPosition = position.coerceIn(0, player.duration)
                if (validPosition > 0) {
                    player.seekTo(validPosition)
                    _currentPosition.value = validPosition
                    Log.d(TAG, "⏩ Seeked to ${formatTime(validPosition)}")
                }

                _currentAudioItem.value = audio
                _isAudioMode.value = true
                _duration.value = player.duration

                // UPDATE: Build queue properly
                buildQueueFromAllAudio(audio)

                player.playWhenReady = false

                Log.d(TAG, "✅ Audio restored successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to restore audio", e)
        }
    }

    private suspend fun restoreVideo(video: VideoItem, position: Long) {
        try {
            Log.d(TAG, "🎬 Restoring video: ${video.title} at ${formatTime(position)}")

            player.stop()
            player.clearMediaItems()

            val mediaItem = video.toMediaItem()

            player.setMediaItem(mediaItem)
            player.prepare()

            var waitAttempts = 0
            while (player.playbackState != Player.STATE_READY && waitAttempts < 30) {
                delay(100)
                waitAttempts++
            }

            if (player.playbackState == Player.STATE_READY) {
                val validPosition = position.coerceIn(0, player.duration)
                if (validPosition > 0) {
                    player.seekTo(validPosition)
                    _currentPosition.value = validPosition
                    Log.d(TAG, "⏩ Seeked to ${formatTime(validPosition)}")
                }

                _currentVideoItem.value = video
                _isAudioMode.value = false
                _duration.value = player.duration

                // UPDATE: Build queue properly
                buildQueueFromAllVideo(video)

                player.playWhenReady = false

                Log.d(TAG, "✅ Video restored successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to restore video", e)
        }
    }

    private fun formatTime(ms: Long): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val secs = seconds % 60
        return "${minutes}:${secs.toString().padStart(2, '0')}"
    }

    fun loadAudioItems() {
        viewModelScope.launch {
            val items = mediaScanner.scanAudioFiles(_audioSortOption.value)
            _audioItems.value = items
            Log.d(TAG, "🎵 Loaded ${items.size} audio items")
        }
    }

    fun loadVideoItems() {
        viewModelScope.launch {
            val items = mediaScanner.scanVideoFiles(_videoSortOption.value)
            _videoItems.value = items
            Log.d(TAG, "🎬 Loaded ${items.size} video items")
        }
    }

    // UPDATE: Improved playVideo with proper queue building
    fun playVideo(video: VideoItem, autoPlay: Boolean = true) {
        if (lastPlayedMediaId == video.id && _currentVideoItem.value?.id == video.id && !autoPlay) {
            Log.d(TAG, "⏭️ Ignoring duplicate playVideo call")
            return
        }

        lastPlayedMediaId = video.id
        Log.d(TAG, "=== PLAY VIDEO START ===")
        Log.d(TAG, "Title: ${video.title}")

        viewModelScope.launch {
            val savedPosition = _videoPositions.value[video.id] ?: 0L
            if (savedPosition > 1000) {
                Log.d(TAG, "🔄 Found saved position: ${formatTime(savedPosition)}")
            }

            try {
                player.stop()
                player.clearMediaItems()

                val mediaItem = video.toMediaItem()

                player.setMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = autoPlay

                _currentVideoItem.value = video
                _isAudioMode.value = false

                // BUILD proper queue
                buildQueueFromAllVideo(video)

                preferencesManager.addRecentlyPlayedVideo(video.id)
                _recentlyPlayedVideoIds.value =
                    preferencesManager.appPreferences.first().recentlyPlayedVideos.map { it.videoId }

                var attempts = 0
                while (player.playbackState != Player.STATE_READY && attempts < 20) {
                    delay(100)
                    attempts++
                }

                if (player.playbackState == Player.STATE_READY) {
                    if (savedPosition > 0) {
                        val validPosition = savedPosition.coerceIn(0, player.duration)
                        player.seekTo(validPosition)
                        Log.d(TAG, "⏩ Seeking to saved position: ${formatTime(validPosition)}")
                    }

                    sessionManager.saveSession(
                        mediaId = video.id,
                        position = if (savedPosition > 0) savedPosition else 0L,
                        isAudioMode = false,
                        force = true
                    )
                }

                Log.d(TAG, "=== PLAY VIDEO END ===")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error playing video", e)
            }
        }
    }

    // NEW: Queue building helper functions
    private fun buildQueueFromAllAudio(currentAudio: AudioItem) {
        val allAudio = _audioItems.value
        val queue = allAudio.map { MediaItemInfo(it.id, it.title, true) }
        _currentQueue.value = queue
        _currentQueueIndex.value = allAudio.indexOfFirst { it.id == currentAudio.id }.coerceAtLeast(0)
        currentPlaylist = allAudio.map { it.id }
        currentIndex = _currentQueueIndex.value
    }

    private fun buildQueueFromAllVideo(currentVideo: VideoItem) {
        val allVideo = _videoItems.value
        val queue = allVideo.map { MediaItemInfo(it.id, it.title, false) }
        _currentQueue.value = queue
        _currentQueueIndex.value = allVideo.indexOfFirst { it.id == currentVideo.id }.coerceAtLeast(0)
        currentPlaylist = allVideo.map { it.id }
        currentIndex = _currentQueueIndex.value
    }

    private fun updateCurrentQueueIndex() {
        val currentId = if (_isAudioMode.value) _currentAudioItem.value?.id else _currentVideoItem.value?.id
        if (currentId != null) {
            val newIndex = _currentQueue.value.indexOfFirst { it.id == currentId }
            if (newIndex >= 0) {
                _currentQueueIndex.value = newIndex
                currentIndex = newIndex
            }
        }
    }

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

    fun seekTo(position: Long) {
        player.seekTo(position)
        _currentPosition.value = position

        viewModelScope.launch {
            delay(500)
            saveCurrentSession(force = true)
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

    fun setAudioViewMode(mode: ViewMode) {
        _audioViewMode.value = mode
        viewModelScope.launch {
            preferencesManager.saveAudioViewMode(mode)
        }
    }

    fun setVideoViewMode(mode: ViewMode) {
        _videoViewMode.value = mode
        viewModelScope.launch {
            preferencesManager.saveVideoViewMode(mode)
        }
    }

    fun setAudioSort(sort: SortOption) {
        _audioSortOption.value = sort
        viewModelScope.launch {
            preferencesManager.saveAudioSort(sort)
            loadAudioItems()
        }
    }

    fun setVideoSort(sort: SortOption) {
        _videoSortOption.value = sort
        viewModelScope.launch {
            preferencesManager.saveVideoSort(sort)
            loadVideoItems()
        }
    }

    private fun saveCurrentSession(force: Boolean = false) {
        if (isLoadingMedia || !hasRestoredSession) {
            Log.d(TAG, "⚠️ Skipping save (isLoading: $isLoadingMedia, hasRestored: $hasRestoredSession)")
            return
        }

        val isAudio = _isAudioMode.value
        val currentMediaId = (if (isAudio) _currentAudioItem.value?.id else _currentVideoItem.value?.id) ?: 0L

        if (currentMediaId == 0L) {
            Log.d(TAG, "⚠️ Skipping save (no mediaId)")
            return
        }

        var currentPos = player.currentPosition
        if (player.playbackState == Player.STATE_ENDED) {
            currentPos = 0L
        }

        viewModelScope.launch {
            sessionManager.saveSession(
                mediaId = currentMediaId,
                position = currentPos,
                isAudioMode = isAudio,
                force = force
            )

            if (isAudio) {
                preferencesManager.saveAudioPosition(currentMediaId, currentPos)
            } else {
                preferencesManager.saveVideoPosition(currentMediaId, currentPos)
            }
        }
    }

    override fun onCleared() {
        saveCurrentSession(force = true)
        stopPositionTracking()
        sleepTimerJob?.cancel()

        if (fallbackPlayer.playbackState != Player.STATE_IDLE) {
            (fallbackPlayer as? ExoPlayer)?.release()
        }
        controllerFuture?.let { MediaController.releaseFuture(it) }
        super.onCleared()

        Log.d(TAG, "🛑 PlayerViewModel cleared")
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        player.setPlaybackSpeed(speed)
    }

    fun createNewPlaylist(name: String, selectedAudioIds: List<Long>) {
        viewModelScope.launch {
            preferencesManager.createPlaylist(name, selectedAudioIds)
        }
    }
}

// NEW: Data classes for enhanced features
data class ABLoopState(
    val pointA: Long? = null,
    val pointB: Long? = null,
    val isActive: Boolean = false
)

data class MediaItemInfo(
    val id: Long,
    val title: String,
    val isAudio: Boolean
)