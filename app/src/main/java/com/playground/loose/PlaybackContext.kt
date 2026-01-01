package com.playground.loose

/**
 * Represents the three distinct playback contexts in the app
 */
enum class PlaybackContext {
    /**
     * No media is currently loaded or playing
     */
    NONE,

    /**
     * Pure audio playback (MP3, FLAC, etc.)
     * - Uses AudioPlayerScreen
     * - Managed by AudioPlayerViewModel
     * - Has its own queue and positions
     */
    AUDIO,

    /**
     * Video playback with visual player
     * - Uses VideoPlayerScreen
     * - Managed by VideoPlayerViewModel
     * - Has its own queue and positions
     */
    VIDEO_VISUAL,

    /**
     * Video playing in audio-only mode (screen off capable)
     * - Uses VideoAsAudioPlayerScreen
     * - Managed by VideoPlayerViewModel (same as VIDEO_VISUAL)
     * - Shares positions with VIDEO_VISUAL (same underlying video)
     * - Different UI with audio-focused controls
     */
    VIDEO_AUDIO_ONLY;

    /**
     * Check if this context is video-related (visual or audio-only)
     */
    fun isVideoContext(): Boolean {
        return this == VIDEO_VISUAL || this == VIDEO_AUDIO_ONLY
    }

    /**
     * Check if this context is audio-related (pure audio or video-as-audio)
     */
    fun isAudioUIContext(): Boolean {
        return this == AUDIO || this == VIDEO_AUDIO_ONLY
    }

    /**
     * Check if any media is playing
     */
    fun hasMedia(): Boolean {
        return this != NONE
    }
}