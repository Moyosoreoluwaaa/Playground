package com.playground.loose

import android.app.Application
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
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
@OptIn(UnstableApi::class)
class AudioPlayerViewModel
    (
    application: Application,
    private val player: Player,
    private val sessionManager: SessionManager,
    private val sharedViewModel: SharedMediaViewModel // NEW: Reference to parent
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

            override fun onMediaItemTransition(
                mediaItem: androidx.media3.common.MediaItem?,
                reason: Int
            ) {
                // Update current audio from player when auto-advancing
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    updateCurrentAudioFromPlayer()
                }
            }
        })
    }

    // ============================================
    // AUDIO PLAYBACK FUNCTIONS
    // ============================================

    fun playAudio(
        audio: AudioItem,
        autoPlay: Boolean = true
    ) {
        if (lastPlayedAudioId == audio.id && _currentAudioItem.value?.id == audio.id && !autoPlay) {
            Log.d(TAG, "⏭️ Ignoring duplicate playAudio call")
            return
        }

        lastPlayedAudioId = audio.id

        viewModelScope.launch {
            val savedPosition = _audioPositions.value[audio.id] ?: 0L
            val currentRepeatMode = repeatModeManager.getCurrentRepeatMode()

            // FIXED: Load full queue when RepeatMode.ALL
            val success = if (currentRepeatMode == RepeatMode.ALL) {
                val allAudio = _audioItems.value
                val startIndex = allAudio.indexOfFirst { it.id == audio.id }.coerceAtLeast(0)

                audioManager.playAudioPlaylist(
                    playlist = allAudio,
                    startIndex = startIndex,
                    savedPosition = savedPosition,
                    repeatMode = Player.REPEAT_MODE_ALL
                )
            } else {
                audioManager.playSingleAudio(
                    audio = audio,
                    savedPosition = savedPosition,
                    autoPlay = autoPlay,
                    repeatMode = currentRepeatMode.toPlayerRepeatMode()
                )
            }

            if (success) {
                _currentAudioItem.value = audio
                _isPlaylistMode.value = (currentRepeatMode == RepeatMode.ALL)

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
        if (player.mediaItemCount > 1) {
            // Player has a queue, use built-in navigation
            if (player.hasNextMediaItem()) {
                player.seekToNextMediaItem()
                updateCurrentAudioFromPlayer()
            } else if (repeatModeManager.getCurrentRepeatMode() == RepeatMode.ALL) {
                player.seekTo(0, 0)
                player.play()
                updateCurrentAudioFromPlayer()
            }
            return
        }

        // Fallback: manual queue management
        val nextItem = queueManager.getNextItem()
        if (nextItem?.isAudio == true) {
            _audioItems.value.find { it.id == nextItem.id }?.let {
                playAudio(it, autoPlay = true)
            }
        }
    }

    fun playPrevious() {
        if (player.mediaItemCount > 1) {
            // Player has a queue, use built-in navigation
            if (player.currentPosition > 3000) {
                player.seekTo(0)
            } else if (player.hasPreviousMediaItem()) {
                player.seekToPreviousMediaItem()
                updateCurrentAudioFromPlayer()
            } else if (repeatModeManager.getCurrentRepeatMode() == RepeatMode.ALL) {
                player.seekTo(player.mediaItemCount - 1, 0)
                player.play()
                updateCurrentAudioFromPlayer()
            }
            return
        }

        // Fallback: manual queue management
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
        val oldMode = repeatModeManager.getCurrentRepeatMode()
        repeatModeManager.toggleRepeatMode()
        val newMode = repeatModeManager.getCurrentRepeatMode()

        // Apply immediately to player
        player.repeatMode = newMode.toPlayerRepeatMode()

        viewModelScope.launch {
            preferencesManager.saveRepeatMode(newMode)
        }

        Log.d(TAG, "🔄 Repeat mode: $oldMode → $newMode")

        // If switching to ALL and currently playing single, reload with full queue
        if (newMode == RepeatMode.ALL && player.mediaItemCount == 1) {
            _currentAudioItem.value?.let { audio ->
                playAudio(audio, autoPlay = player.isPlaying)
            }
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
    // CONTEXT SWITCHING (NEW!)
    // ============================================

    /**
     * Clear audio state when switching to video context
     */
    fun clearStateForContextSwitch() {
        Log.d(TAG, "🧹 Clearing audio state for context switch")

        // Save current position before stopping
        saveCurrentAudioSession(force = true)

        // Stop playback
        player.stop()
        player.clearMediaItems()

        // Clear current audio
        _currentAudioItem.value = null
        _isPlaylistMode.value = false

        // Clear queue
        queueManager.clearQueue()

        Log.d(TAG, "✅ Audio state cleared")
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
                Log.d(TAG, "🔄 Updated current audio: ${it.title}")
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

            RepeatMode.ALL -> {
                // Player handles this automatically if queue is loaded
                if (player.mediaItemCount > 1) {
                    // Auto-advance handled by player
                } else {
                    playNext()
                }
            }

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

// Extension function
fun RepeatMode.toPlayerRepeatMode(): Int {
    return when (this) {
        RepeatMode.OFF -> Player.REPEAT_MODE_OFF
        RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        RepeatMode.ALL -> Player.REPEAT_MODE_ALL
    }
}