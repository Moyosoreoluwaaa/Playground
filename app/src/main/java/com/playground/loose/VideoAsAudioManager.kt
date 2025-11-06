package com.playground.loose

import android.util.Log
import com.playground.loose.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages video-as-audio mode (listening to video with screen off)
 */
class VideoAsAudioManager {
    companion object {
        private const val TAG = "VideoAsAudioManager"
    }

    private val _isVideoAsAudioMode = MutableStateFlow(false)
    val isVideoAsAudioMode: StateFlow<Boolean> = _isVideoAsAudioMode.asStateFlow()

    private var currentVideoInAudioMode: VideoItem? = null

    /**
     * Switch to video-as-audio mode
     */
    fun enableVideoAsAudioMode(video: VideoItem) {
        Log.d(TAG, "🎵 Enabling video-as-audio mode: ${video.title}")
        currentVideoInAudioMode = video
        _isVideoAsAudioMode.value = true
    }

    /**
     * Return to video player mode
     */
    fun disableVideoAsAudioMode() {
        Log.d(TAG, "🎬 Disabling video-as-audio mode")
        _isVideoAsAudioMode.value = false
        currentVideoInAudioMode = null
    }

    /**
     * Get the video that's playing as audio
     */
    fun getCurrentVideoInAudioMode(): VideoItem? = currentVideoInAudioMode

    /**
     * Check if currently in video-as-audio mode
     */
    fun isInVideoAsAudioMode(): Boolean = _isVideoAsAudioMode.value
}
