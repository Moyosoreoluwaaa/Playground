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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@UnstableApi
class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val mediaScanner = MediaScanner(application)

    // MediaController for background service
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    // Use the controller's player instance
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

    private var currentPlaylist = listOf<Long>()
    private var currentIndex = 0

    init {
        initializeMediaController()
        loadPreferencesAndRestoreSession()
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
                controller = controllerFuture?.get()
                setupPlayerListener()

                // Attach debugger in debug builds
                controller?.attachDebugger()
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun createFallbackPlayer(): Player {
        // Fallback player in case MediaController isn't ready
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
                saveCurrentState()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.let {
                    _duration.value = player.duration
                    saveCurrentState()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _duration.value = player.duration
                }

                if (playbackState == Player.STATE_ENDED) {
                    handlePlaybackEnded()
                }
            }
        })

        // Update position periodically
        viewModelScope.launch {
            while (true) {
                if (player.isPlaying) {
                    _currentPosition.value = player.currentPosition
                }
                kotlinx.coroutines.delay(500)
            }
        }
    }

    private fun handlePlaybackEnded() {
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                player.seekTo(0)
                player.play()
            }
            RepeatMode.ALL -> {
                playNext()
            }
            RepeatMode.OFF -> {
                // Stop at end
            }
        }
    }

    private var isRestoringSession = false

    private fun loadPreferencesAndRestoreSession() {
        viewModelScope.launch {
            preferencesManager.appPreferences.first().let { prefs ->
                _audioViewMode.value = prefs.audioViewMode
                _videoViewMode.value = prefs.videoViewMode
                _audioSortOption.value = prefs.audioSortOption
                _videoSortOption.value = prefs.videoSortOption
                _repeatMode.value = prefs.lastPlaybackState.repeatMode
                _isAudioMode.value = prefs.lastPlaybackState.isAudioMode

                loadAudioItems()
                loadVideoItems()

                val state = prefs.lastPlaybackState
                if (state.mediaId != 0L && !isRestoringSession) {
                    isRestoringSession = true
                    restorePlaybackSession(state)
                }
            }
        }
    }

    private suspend fun restorePlaybackSession(state: PlaybackState) {
        if (state.isAudioMode) {
            _audioItems.value.find { it.id == state.mediaId }?.let { audio ->
                playAudio(audio, false)
                player.seekTo(state.position)
                player.playWhenReady = state.isPlaying
            }
        } else {
            _videoItems.value.find { it.id == state.mediaId }?.let { video ->
                playVideo(video, false)
                player.seekTo(state.position)
                player.playWhenReady = state.isPlaying
            }
        }

        currentPlaylist = state.currentPlaylistIds
        currentIndex = state.currentIndex
    }

    fun loadAudioItems() {
        viewModelScope.launch {
            val items = mediaScanner.scanAudioFiles(_audioSortOption.value)
            _audioItems.value = items
        }
    }

    fun loadVideoItems() {
        viewModelScope.launch {
            val items = mediaScanner.scanVideoFiles(_videoSortOption.value)
            _videoItems.value = items
        }
    }

    private var lastPlayedMediaId = 0L

    fun playAudio(audio: AudioItem, autoPlay: Boolean = true) {
        if (lastPlayedMediaId == audio.id && _currentAudioItem.value?.id == audio.id) {
            Log.d(TAG, "Ignoring duplicate playAudio call for ${audio.title}")
            return
        }

        lastPlayedMediaId = audio.id
        Log.d(TAG, "=== PLAY AUDIO START ===")
        Log.d(TAG, "Title: ${audio.title}")
        Log.d(TAG, "URI: ${audio.uri}")
        Log.d(TAG, "AutoPlay: $autoPlay")

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

            Log.d(TAG, "Player state after setup:")
            Log.d(TAG, "  - PlaybackState: ${player.playbackState}")
            Log.d(TAG, "  - PlayWhenReady: ${player.playWhenReady}")
            Log.d(TAG, "  - IsPlaying: ${player.isPlaying}")

            _currentAudioItem.value = audio
            _isAudioMode.value = true
            currentPlaylist = _audioItems.value.map { it.id }
            currentIndex = _audioItems.value.indexOf(audio)

            saveCurrentState()
            Log.d(TAG, "=== PLAY AUDIO END ===")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio", e)
        }
    }

    fun playVideo(video: VideoItem, autoPlay: Boolean = true) {
        if (lastPlayedMediaId == video.id && _currentVideoItem.value?.id == video.id) {
            Log.d(TAG, "Ignoring duplicate playVideo call for ${video.title}")
            return
        }

        lastPlayedMediaId = video.id
        Log.d(TAG, "=== PLAY VIDEO START ===")
        Log.d(TAG, "Title: ${video.title}")
        Log.d(TAG, "URI: ${video.uri}")
        Log.d(TAG, "AutoPlay: $autoPlay")
        Log.d(TAG, "Resolution: ${video.width}x${video.height}")

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

            Log.d(TAG, "Player state after setup:")
            Log.d(TAG, "  - PlaybackState: ${player.playbackState}")
            Log.d(TAG, "  - PlayWhenReady: ${player.playWhenReady}")
            Log.d(TAG, "  - IsPlaying: ${player.isPlaying}")

            _currentVideoItem.value = video
            _isAudioMode.value = false
            currentPlaylist = _videoItems.value.map { it.id }
            currentIndex = _videoItems.value.indexOf(video)

            saveCurrentState()
            Log.d(TAG, "=== PLAY VIDEO END ===")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing video", e)
        }
    }

    companion object {
        private const val TAG = "PlayerViewModel"
    }

    fun playPause() {
        Log.d(TAG, "playPause called - Current state: isPlaying=${player.isPlaying}")

        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.playbackState == Player.STATE_IDLE) {
                Log.d(TAG, "Player was idle, re-preparing...")
                player.prepare()
            }
            player.play()
        }

        Log.d(TAG, "playPause result: isPlaying=${player.isPlaying}, playWhenReady=${player.playWhenReady}")
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
        _currentPosition.value = position
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
        player.seekTo(newPosition)
    }

    fun skipBackward() {
        val newPosition = (player.currentPosition - 10000).coerceAtLeast(0)
        player.seekTo(newPosition)
    }

    fun toggleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        saveCurrentState()
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

    private fun saveCurrentState() {
        viewModelScope.launch {
            val mediaId = if (_isAudioMode.value) {
                _currentAudioItem.value?.id ?: 0L
            } else {
                _currentVideoItem.value?.id ?: 0L
            }

            val state = PlaybackState(
                mediaId = mediaId,
                position = player.currentPosition,
                isPlaying = player.isPlaying,
                repeatMode = _repeatMode.value,
                isAudioMode = _isAudioMode.value,
                currentPlaylistIds = currentPlaylist,
                currentIndex = currentIndex
            )

            preferencesManager.savePlaybackState(state)
        }
    }

    override fun onCleared() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        super.onCleared()
    }
}