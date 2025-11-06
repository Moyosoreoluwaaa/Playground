package com.playground.loose

import android.util.Log
import com.playground.loose.AudioItem
import com.playground.loose.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages playback queue for audio and video
 */
class MediaQueueManager {
    companion object {
        private const val TAG = "MediaQueueManager"
    }

    private val _currentQueue = MutableStateFlow<List<MediaItemInfo>>(emptyList())
    val currentQueue: StateFlow<List<MediaItemInfo>> = _currentQueue.asStateFlow()

    private val _currentQueueIndex = MutableStateFlow(0)
    val currentQueueIndex: StateFlow<Int> = _currentQueueIndex.asStateFlow()

    /**
     * Build queue from all audio items
     */
    fun buildAudioQueue(allAudio: List<AudioItem>, currentAudio: AudioItem) {
        val queue = allAudio.map { MediaItemInfo(it.id, it.title, true) }
        _currentQueue.value = queue
        _currentQueueIndex.value = allAudio.indexOfFirst { it.id == currentAudio.id }.coerceAtLeast(0)
        Log.d(TAG, "🎵 Audio queue built: ${queue.size} items")
    }

    /**
     * Build queue from all video items
     */
    fun buildVideoQueue(allVideo: List<VideoItem>, currentVideo: VideoItem) {
        val queue = allVideo.map { MediaItemInfo(it.id, it.title, false) }
        _currentQueue.value = queue
        _currentQueueIndex.value = allVideo.indexOfFirst { it.id == currentVideo.id }.coerceAtLeast(0)
        Log.d(TAG, "🎬 Video queue built: ${queue.size} items")
    }

    /**
     * Build queue from audio playlist
     */
    fun buildPlaylistQueue(playlist: List<AudioItem>, startIndex: Int = 0) {
        val queue = playlist.map { MediaItemInfo(it.id, it.title, true) }
        _currentQueue.value = queue
        _currentQueueIndex.value = startIndex
        Log.d(TAG, "📀 Playlist queue built: ${queue.size} items, start=$startIndex")
    }

    /**
     * Get next item in queue
     */
    fun getNextItem(): MediaItemInfo? {
        if (_currentQueue.value.isEmpty()) return null
        val nextIndex = (_currentQueueIndex.value + 1) % _currentQueue.value.size
        return _currentQueue.value.getOrNull(nextIndex)
    }

    /**
     * Get previous item in queue
     */
    fun getPreviousItem(): MediaItemInfo? {
        if (_currentQueue.value.isEmpty()) return null
        val prevIndex = if (_currentQueueIndex.value - 1 < 0) {
            _currentQueue.value.size - 1
        } else {
            _currentQueueIndex.value - 1
        }
        return _currentQueue.value.getOrNull(prevIndex)
    }

    /**
     * Update current queue index
     */
    fun updateQueueIndex(mediaId: Long) {
        val newIndex = _currentQueue.value.indexOfFirst { it.id == mediaId }
        if (newIndex >= 0) {
            _currentQueueIndex.value = newIndex
        }
    }

    /**
     * Clear the queue
     */
    fun clearQueue() {
        _currentQueue.value = emptyList()
        _currentQueueIndex.value = 0
    }
}

data class MediaItemInfo(
    val id: Long,
    val title: String,
    val isAudio: Boolean
)