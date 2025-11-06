package com.playground.loose

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.loose.mediaplayer.playback.AudioPlaybackManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * ViewModel specifically for Audio playback
 * Handles: Audio library, playlists, and audio-only features
 */
class AudioPlayerViewModel(
    application: Application,
    private val player: Player,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "AudioPlayerViewModel"
    }

    // ============ Managers ============
    private val preferencesManager = PreferencesManager(application)
    private val mediaScanner = MediaScanner(application)
    private val audioManager = AudioPlaybackManager(player)
    private val stateManager = PlaybackStateManager(player, viewModelScope)
    private val queueManager = MediaQueueManager()
    private val repeatModeManager = RepeatModeManager(player)
    private val speedManager = PlaybackSpeedManager(player)

    // ============ Audio Library State ============
    private val _audioItems = MutableStateFlow<List<AudioItem>>(emptyList())
    val audioItems: StateFlow<List<AudioItem>> = _audioItems.asStateFlow()

    private val _currentAudioItem = MutableStateFlow<AudioItem?>(null)
    val currentAudioItem: StateFlow<AudioItem?> = _currentAudioItem.asStateFlow()

    private val _audioPositions = MutableStateFlow<Map<Long, Long>>(emptyMap())
    val audioPositions: StateFlow<Map<Long, Long>> = _audioPositions.asStateFlow()

    private val _audioPlaylists = MutableStateFlow<List<AudioPlaylist>>(emptyList())
    val audioPlaylists: StateFlow<List<AudioPlaylist>> = _audioPlaylists.asStateFlow()

    private val _isPlaylistMode = MutableStateFlow(false)
    val isPlaylistMode: StateFlow<Boolean> = _isPlaylistMode.asStateFlow()

    // ============ UI State ============
    private val _audioViewMode = MutableStateFlow(ViewMode.LIST)
    val audioViewMode: StateFlow<ViewMode> = _audioViewMode.asStateFlow()

    private val _audioSortOption = MutableStateFlow(SortOption.NAME)
    val audioSortOption: StateFlow<SortOption> = _audioSortOption.asStateFlow()

    // ============ Exposed Manager States ============
    val isPlaying: StateFlow<Boolean> = stateManager.isPlaying
    val currentPosition: StateFlow<Long> = stateManager.currentPosition
    val duration: StateFlow<Long> = stateManager.duration
    val abLoopState: StateFlow<ABLoopState> = stateManager.abLoopState
    val sleepTimerRemaining: StateFlow<Long> = stateManager.sleepTimerRemaining
    val currentQueue: StateFlow<List<MediaItemInfo>> = queueManager.currentQueue
    val currentQueueIndex: StateFlow<Int> = queueManager.currentQueueIndex
    val repeatMode: StateFlow<RepeatMode> = repeatModeManager.repeatMode
    val playbackSpeed: StateFlow<Float> = speedManager.playbackSpeed

    // ============ Internal State ============
    private var lastPlayedAudioId = 0L

    init {
        Log.d(TAG, "🎵 AudioPlayerViewModel initializing...")
        setupPlayerListener()
        observePreferences()
        loadAudioItems()
    }

    // ============================================
    // PLAYER LISTENER
    // ============================================

    private fun setupPlayerListener() {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                stateManager.updatePlaybackState(isPlaying, player.duration)
                Log.d(TAG, "🎵 Is playing changed: $isPlaying")

                if (!isPlaying && player.currentPosition > 0) {
                    viewModelScope.launch {
                        delay(100)
                        saveCurrentAudioSession(force = true)
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
    // AUDIO PLAYBACK FUNCTIONS
    // ============================================

    fun playAudio(audio: AudioItem, autoPlay: Boolean = true) {
        if (lastPlayedAudioId == audio.id && _currentAudioItem.value?.id == audio.id && !autoPlay) {
            Log.d(TAG, "⏭️ Ignoring duplicate playAudio call")
            return
        }

        lastPlayedAudioId = audio.id

        viewModelScope.launch {
            val savedPosition = _audioPositions.value[audio.id] ?: 0L

            val success = audioManager.playSingleAudio(audio, savedPosition, autoPlay)

            if (success) {
                _currentAudioItem.value = audio
                _isPlaylistMode.value = false

                queueManager.buildAudioQueue(_audioItems.value, audio)

                sessionManager.saveSession(
                    mediaId = audio.id,
                    position = if (savedPosition > 0) savedPosition else 0L,
                    isAudioMode = true,
                    force = true
                )
            }
        }
    }

    fun playSelectedAudio(selection: List<AudioItem>, startIndex: Int = 0) {
        if (selection.isEmpty()) return

        viewModelScope.launch {
            val startAudio = selection[startIndex]
            val savedPosition = _audioPositions.value[startAudio.id] ?: 0L

            val success = audioManager.playAudioPlaylist(
                playlist = selection,
                startIndex = startIndex,
                savedPosition = savedPosition,
                repeatMode = repeatModeManager.getCurrentRepeatMode().toPlayerRepeatMode()
            )

            if (success) {
                _currentAudioItem.value = startAudio
                _isPlaylistMode.value = true

                queueManager.buildPlaylistQueue(selection, startIndex)

                Log.d(TAG, "✅ Playlist mode: ${selection.size} items")
            }
        }
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
        if (_isPlaylistMode.value && player.mediaItemCount > 1) {
            audioManager.playNextAudio()
            updateCurrentAudioFromPlayer()
            return
        }

        val nextItem = queueManager.getNextItem()
        if (nextItem?.isAudio == true) {
            _audioItems.value.find { it.id == nextItem.id }?.let {
                playAudio(it, autoPlay = true)
            }
        }
    }

    fun playPrevious() {
        if (_isPlaylistMode.value && player.mediaItemCount > 1) {
            audioManager.playPreviousAudio()
            updateCurrentAudioFromPlayer()
            return
        }

        val prevItem = queueManager.getPreviousItem()
        if (prevItem?.isAudio == true) {
            _audioItems.value.find { it.id == prevItem.id }?.let {
                playAudio(it, autoPlay = true)
            }
        }
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
        viewModelScope.launch {
            delay(500)
            saveCurrentAudioSession(force = true)
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

    fun setABLoopPointA() = stateManager.setABLoopPointA()
    fun setABLoopPointB() = stateManager.setABLoopPointB()
    fun clearABLoop() = stateManager.clearABLoop()

    fun startSleepTimer(durationMs: Long) = stateManager.startSleepTimer(durationMs)
    fun cancelSleepTimer() = stateManager.cancelSleepTimer()

    // ============================================
    // UI SETTINGS
    // ============================================

    fun setAudioViewMode(mode: ViewMode) {
        _audioViewMode.value = mode
        viewModelScope.launch {
            preferencesManager.saveAudioViewMode(mode)
        }
    }

    fun setAudioSort(sort: SortOption) {
        _audioSortOption.value = sort
        viewModelScope.launch {
            preferencesManager.saveAudioSort(sort)
            loadAudioItems()
        }
    }

    // ============================================
    // PLAYLIST MANAGEMENT
    // ============================================

    fun createNewPlaylist(name: String, selectedAudioIds: List<Long>) {
        viewModelScope.launch {
            preferencesManager.createPlaylist(name, selectedAudioIds)
        }
    }

    // ============================================
    // INTERNAL HELPERS
    // ============================================

    private fun updateCurrentAudioFromPlayer() {
        val currentId = audioManager.getCurrentAudioId()
        currentId?.let { id ->
            queueManager.updateQueueIndex(id)
            _audioItems.value.find { it.id == id }?.let {
                _currentAudioItem.value = it
            }
        }
    }

    private fun handlePlaybackEnded() {
        saveCurrentAudioSession(force = true)

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
                _audioViewMode.value = prefs.audioViewMode
                _audioSortOption.value = prefs.audioSortOption
                repeatModeManager.setRepeatMode(prefs.lastPlaybackState.repeatMode)
                _audioPositions.value = prefs.audioPositions
            }
            .launchIn(viewModelScope)

        preferencesManager.audioPlaylists
            .onEach { playlists ->
                _audioPlaylists.value = playlists
            }
            .launchIn(viewModelScope)
    }

    fun loadAudioItems() {
        viewModelScope.launch {
            val items = mediaScanner.scanAudioFiles(_audioSortOption.value)
            _audioItems.value = items
            Log.d(TAG, "🎵 Loaded ${items.size} audio items")
        }
    }

    private fun saveCurrentAudioSession(force: Boolean = false) {
        val currentMediaId = _currentAudioItem.value?.id ?: return
        var currentPos = player.currentPosition

        if (player.playbackState == Player.STATE_ENDED) {
            currentPos = 0L
        }

        viewModelScope.launch {
            sessionManager.saveSession(
                mediaId = currentMediaId,
                position = currentPos,
                isAudioMode = true,
                force = force
            )

            preferencesManager.saveAudioPosition(currentMediaId, currentPos)
        }
    }

    public override fun onCleared() {
        saveCurrentAudioSession(force = true)
        stateManager.cleanup()
        super.onCleared()
        Log.d(TAG, "🛑 AudioPlayerViewModel cleared")
    }
}