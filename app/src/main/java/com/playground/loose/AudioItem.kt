package com.playground.loose

import android.net.Uri

// Media Items
data class AudioItem(
    val id: Long,
    val uri: Uri,
    val title: String,
    val artist: String?,
    val album: String?,
    val duration: Long,
    val size: Long,
    val dateAdded: Long,
    val albumArtUri: Uri?,
    val path: String
)

data class VideoItem(
    val id: Long,
    val uri: Uri,
    val title: String,
    val duration: Long,
    val size: Long,
    val dateAdded: Long,
    val thumbnailUri: Uri?,
    val path: String,
    val width: Int,
    val height: Int
)

// Recently played item
data class RecentlyPlayedVideo(
    val videoId: Long,
    val playedAt: Long // timestamp
)

// Playback State
data class PlaybackState(
    val mediaId: Long = 0L,
    val position: Long = 0L,
    val isPlaying: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val isAudioMode: Boolean = true,
    val currentPlaylistIds: List<Long> = emptyList(),
    val currentIndex: Int = 0
)

enum class RepeatMode {
    OFF,        // No repeat
    ONE,        // Repeat one
    ALL         // Repeat all
}

enum class SortOption {
    NAME,
    DATE_ADDED,
    DURATION,
    SIZE
}

enum class ViewMode {
    LIST,
    GRID
}

// App Preferences
data class AppPreferences(
    val audioViewMode: ViewMode = ViewMode.LIST,
    val videoViewMode: ViewMode = ViewMode.LIST,
    val audioSortOption: SortOption = SortOption.NAME,
    val videoSortOption: SortOption = SortOption.NAME,
    val selectedTheme: AppTheme = AppTheme.DARK,
    val lastPlaybackState: PlaybackState = PlaybackState(),
    val recentlyPlayedVideos: List<RecentlyPlayedVideo> = emptyList()
)

enum class AppTheme {
    LIGHT,
    DARK,
    AMOLED,
    CUSTOM
}