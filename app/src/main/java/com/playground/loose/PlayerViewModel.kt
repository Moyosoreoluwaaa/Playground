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

    val player: Player
        get() = controller ?: createFallbackPlayer()

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

    private var currentPlaylist = listOf<Long>()
    private var currentIndex = 0

    private var positionTrackingJob: Job? = null
    private var hasRestoredSession = false
    private var isLoadingMedia = false

    companion object {
        private const val TAG = "PlayerViewModel"
    }

    init {
        Log.d(TAG, "🚀 PlayerViewModel initializing...")
        loadMediaAndRestore()
    }

    private fun loadMediaAndRestore() {
        viewModelScope.launch {
            try {
                // 1. Load preferences first
                Log.d(TAG, "📋 Loading preferences...")
                preferencesManager.appPreferences.first().let { prefs ->
                    _audioViewMode.value = prefs.audioViewMode
                    _videoViewMode.value = prefs.videoViewMode
                    _audioSortOption.value = prefs.audioSortOption
                    _videoSortOption.value = prefs.videoSortOption
                    _repeatMode.value = prefs.lastPlaybackState.repeatMode
                    _isAudioMode.value = prefs.lastPlaybackState.isAudioMode
                    _recentlyPlayedVideoIds.value = prefs.recentlyPlayedVideos.map { it.videoId }
                    Log.d(TAG, "✅ Preferences loaded")
                }

                // 2. Get saved session BEFORE loading media
                val savedSession = sessionManager.getSession()
                Log.d(TAG, "📂 Saved session: ${savedSession?.let { "mediaId=${it.mediaId}, pos=${formatTime(it.position)}" } ?: "none"}")

                // 3. Initialize MediaController
                Log.d(TAG, "🔌 Initializing MediaController...")
                initializeMediaController()

                // 4. Load media items
                Log.d(TAG, "📂 Loading media items...")
                isLoadingMedia = true
                loadAudioItems()
                loadVideoItems()

                // Wait for items to load
                var attempts = 0
                while (attempts < 25 && (_audioItems.value.isEmpty() || _videoItems.value.isEmpty())) {
                    delay(200)
                    attempts++
                }
                isLoadingMedia = false
                Log.d(TAG, "✅ Media items loaded: ${_audioItems.value.size} audio, ${_videoItems.value.size} video")

                // 5. Wait for controller to be ready
                attempts = 0
                while (!isControllerReady && attempts < 50) {
                    delay(100)
                    attempts++
                }

                if (!isControllerReady) {
                    Log.e(TAG, "❌ MediaController not ready after timeout")
                    return@launch
                }

                Log.d(TAG, "✅ MediaController ready")

                // 6. Restore session if available
                if (savedSession != null && savedSession.mediaId != 0L) {
                    Log.d(TAG, "🎬 Restoring session for mediaId=${savedSession.mediaId}")
                    restoreSessionInternal(savedSession)
                } else {
                    Log.d(TAG, "ℹ️ No session to restore")
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
                    // Save session when pausing (with valid position check)
                    if (player.currentPosition > 1000) {
                        viewModelScope.launch {
                            delay(100) // Small delay to ensure position is stable
                            saveCurrentSession(force = true)
                        }
                    }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.let {
                    _duration.value = player.duration
                    Log.d(TAG, "🎬 Media transition: ${it.mediaMetadata.title}")
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
                    _currentPosition.value = player.currentPosition
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
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                player.seekTo(0)
                player.play()
            }
            RepeatMode.ALL -> playNext()
            RepeatMode.OFF -> {
                saveCurrentSession(force = true)
            }
        }
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
                    logAvailableIds(true)
                }
            } else {
                val video = _videoItems.value.find { it.id == session.mediaId }
                if (video != null) {
                    Log.d(TAG, "✅ Found video: ${video.title}")
                    restoreVideo(video, session.position)
                } else {
                    Log.w(TAG, "⚠️ Video item ${session.mediaId} not found")
                    logAvailableIds(false)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to restore session", e)
        }
    }

    private fun logAvailableIds(isAudio: Boolean) {
        val items = if (isAudio) _audioItems.value else _videoItems.value
        Log.d(TAG, "Available ${if (isAudio) "audio" else "video"} items (first 5):")
        items.take(5).forEach {
            Log.d(TAG, "   ID: ${if (isAudio) (it as AudioItem).id else (it as VideoItem).id}")
        }
    }

    private suspend fun restoreAudio(audio: AudioItem, position: Long) {
        try {
            Log.d(TAG, "🎵 Restoring audio: ${audio.title} at ${formatTime(position)}")

            player.stop()
            player.clearMediaItems()

            val mediaItem = MediaItem.Builder()
                .setUri(audio.uri)
                .setMediaId(audio.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(audio.title)
                        .setArtist(audio.artist)
                        .setAlbumTitle(audio.album)
                        .setArtworkUri(audio.albumArtUri)
                        .build()
                )
                .build()

            player.setMediaItem(mediaItem)
            player.prepare()

            // Wait for player to be ready
            var waitAttempts = 0
            while (player.playbackState != Player.STATE_READY && waitAttempts < 30) {
                delay(100)
                waitAttempts++
            }

            if (player.playbackState == Player.STATE_READY) {
                // Validate and seek to position
                val validPosition = position.coerceIn(0, player.duration)
                if (validPosition > 0) {
                    player.seekTo(validPosition)
                    _currentPosition.value = validPosition
                    Log.d(TAG, "⏩ Seeked to ${formatTime(validPosition)} (duration: ${formatTime(player.duration)})")
                }

                // Update state WITHOUT triggering new save
                _currentAudioItem.value = audio
                _isAudioMode.value = true
                _duration.value = player.duration
                currentPlaylist = _audioItems.value.map { it.id }
                currentIndex = _audioItems.value.indexOf(audio)

                // Don't auto-play on restore
                player.playWhenReady = false

                Log.d(TAG, "✅ Audio restored successfully")
            } else {
                Log.e(TAG, "❌ Player not ready (state=${player.playbackState})")
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

            val mediaItem = MediaItem.Builder()
                .setUri(video.uri)
                .setMediaId(video.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(video.title)
                        .build()
                )
                .build()

            player.setMediaItem(mediaItem)
            player.prepare()

            // Wait for player to be ready
            var waitAttempts = 0
            while (player.playbackState != Player.STATE_READY && waitAttempts < 30) {
                delay(100)
                waitAttempts++
            }

            if (player.playbackState == Player.STATE_READY) {
                // Validate and seek to position
                val validPosition = position.coerceIn(0, player.duration)
                if (validPosition > 0) {
                    player.seekTo(validPosition)
                    _currentPosition.value = validPosition
                    Log.d(TAG, "⏩ Seeked to ${formatTime(validPosition)}")
                }

                // Update state WITHOUT triggering new save
                _currentVideoItem.value = video
                _isAudioMode.value = false
                _duration.value = player.duration
                currentPlaylist = _videoItems.value.map { it.id }
                currentIndex = _videoItems.value.indexOf(video)

                // Don't auto-play on restore
                player.playWhenReady = false

                Log.d(TAG, "✅ Video restored successfully")
            } else {
                Log.e(TAG, "❌ Player not ready (state=${player.playbackState})")
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
            Log.d(TAG, "📁 Loaded ${items.size} audio items")
        }
    }

    fun loadVideoItems() {
        viewModelScope.launch {
            val items = mediaScanner.scanVideoFiles(_videoSortOption.value)
            _videoItems.value = items
            Log.d(TAG, "📁 Loaded ${items.size} video items")
        }
    }

    private var lastPlayedMediaId = 0L

    fun playAudio(audio: AudioItem, autoPlay: Boolean = true) {
        // Prevent duplicate calls
        if (lastPlayedMediaId == audio.id && _currentAudioItem.value?.id == audio.id && !autoPlay) {
            Log.d(TAG, "⏭️ Ignoring duplicate playAudio call")
            return
        }

        lastPlayedMediaId = audio.id
        Log.d(TAG, "=== PLAY AUDIO START ===")
        Log.d(TAG, "Title: ${audio.title}")
        Log.d(TAG, "URI: ${audio.uri}")
        Log.d(TAG, "AutoPlay: $autoPlay")

        viewModelScope.launch {
            try {
                player.stop()
                player.clearMediaItems()

                val mediaItem = MediaItem.Builder()
                    .setUri(audio.uri)
                    .setMediaId(audio.id.toString())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(audio.title)
                            .setArtist(audio.artist)
                            .setAlbumTitle(audio.album)
                            .setArtworkUri(audio.albumArtUri)
                            .build()
                    )
                    .build()

                player.setMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = autoPlay

                // Update state
                _currentAudioItem.value = audio
                _isAudioMode.value = true
                currentPlaylist = _audioItems.value.map { it.id }
                currentIndex = _audioItems.value.indexOf(audio)

                // Wait for player to be ready before saving
                var attempts = 0
                while (player.playbackState != Player.STATE_READY && attempts < 20) {
                    delay(100)
                    attempts++
                }

                // Save initial session at position 0
                if (player.playbackState == Player.STATE_READY) {
                    sessionManager.saveSession(
                        mediaId = audio.id,
                        position = 0L,
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

    fun playVideo(video: VideoItem, autoPlay: Boolean = true) {
        // Prevent duplicate calls
        if (lastPlayedMediaId == video.id && _currentVideoItem.value?.id == video.id && !autoPlay) {
            Log.d(TAG, "⏭️ Ignoring duplicate playVideo call")
            return
        }

        lastPlayedMediaId = video.id
        Log.d(TAG, "=== PLAY VIDEO START ===")
        Log.d(TAG, "Title: ${video.title}")
        Log.d(TAG, "URI: ${video.uri}")
        Log.d(TAG, "AutoPlay: $autoPlay")

        viewModelScope.launch {
            try {
                player.stop()
                player.clearMediaItems()

                val mediaItem = MediaItem.Builder()
                    .setUri(video.uri)
                    .setMediaId(video.id.toString())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(video.title)
                            .build()
                    )
                    .build()

                player.setMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = autoPlay

                _currentVideoItem.value = video
                _isAudioMode.value = false
                currentPlaylist = _videoItems.value.map { it.id }
                currentIndex = _videoItems.value.indexOf(video)

                // Add to recently played
                preferencesManager.addRecentlyPlayedVideo(video.id)
                _recentlyPlayedVideoIds.value =
                    preferencesManager.appPreferences.first().recentlyPlayedVideos.map { it.videoId }

                // Wait for player to be ready before saving
                var attempts = 0
                while (player.playbackState != Player.STATE_READY && attempts < 20) {
                    delay(100)
                    attempts++
                }

                // Save initial session at position 0
                if (player.playbackState == Player.STATE_READY) {
                    sessionManager.saveSession(
                        mediaId = video.id,
                        position = 0L,
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

        // Save session after seek (with delay to ensure position is stable)
        viewModelScope.launch {
            delay(500)
            saveCurrentSession(force = true)
        }
    }

    fun playNext() {
        if (currentPlaylist.isEmpty()) return

        currentIndex = (currentIndex + 1) % currentPlaylist.size
        val nextId = currentPlaylist[currentIndex]

        if (_isAudioMode.value) {
            _audioItems.value.find { it.id == nextId }?.let { playAudio(it) }
        } else {
            _videoItems.value.find { it.id == nextId }?.let { playVideo(it) }
        }
    }

    fun playPrevious() {
        if (currentPlaylist.isEmpty()) return

        currentIndex = if (currentIndex - 1 < 0) currentPlaylist.size - 1 else currentIndex - 1
        val prevId = currentPlaylist[currentIndex]

        if (_isAudioMode.value) {
            _audioItems.value.find { it.id == prevId }?.let { playAudio(it) }
        } else {
            _videoItems.value.find { it.id == prevId }?.let { playVideo(it) }
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

    fun toggleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        viewModelScope.launch {
            preferencesManager.savePlaybackState(
                PlaybackState(repeatMode = _repeatMode.value)
            )
        }
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
        // Don't save during media loading or restoration
        if (isLoadingMedia || !hasRestoredSession) {
            return
        }

        val mediaId = if (_isAudioMode.value) {
            _currentAudioItem.value?.id
        } else {
            _currentVideoItem.value?.id
        } ?: return

        viewModelScope.launch {
            sessionManager.saveSession(
                mediaId = mediaId,
                position = player.currentPosition,
                isAudioMode = _isAudioMode.value,
                force = force
            )
        }
    }

    override fun onCleared() {
        // Save final session state
        saveCurrentSession(force = true)

        stopPositionTracking()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        super.onCleared()

        Log.d(TAG, "🛑 PlayerViewModel cleared")
    }

    fun setPlaybackSpeed(speed: Float) {
        player.setPlaybackSpeed(speed)
    }
}