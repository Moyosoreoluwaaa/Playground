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
data class AudioPlaylist(
    val id: Long,
    val name: String,
    val audioIds: List<Long>, // Stores only IDs of the songs
    val dateCreated: Long
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

// Enhanced SortOption with ascending/descending support

enum class SortOption {
    NAME_ASC,
    NAME_DESC,
    DATE_ADDED_ASC,
    DATE_ADDED_DESC,
    DURATION_ASC,
    DURATION_DESC,
    SIZE_ASC,
    SIZE_DESC;

    fun getDisplayName(): String = when (this) {
        NAME_ASC -> "Name (A-Z)"
        NAME_DESC -> "Name (Z-A)"
        DATE_ADDED_ASC -> "Date Added (Oldest)"
        DATE_ADDED_DESC -> "Date Added (Newest)"
        DURATION_ASC -> "Duration (Shortest)"
        DURATION_DESC -> "Duration (Longest)"
        SIZE_ASC -> "Size (Smallest)"
        SIZE_DESC -> "Size (Largest)"
    }

    fun isAscending(): Boolean = when (this) {
        NAME_ASC, DATE_ADDED_ASC, DURATION_ASC, SIZE_ASC -> true
        else -> false
    }

    fun getCategory(): SortCategory = when (this) {
        NAME_ASC, NAME_DESC -> SortCategory.NAME
        DATE_ADDED_ASC, DATE_ADDED_DESC -> SortCategory.DATE_ADDED
        DURATION_ASC, DURATION_DESC -> SortCategory.DURATION
        SIZE_ASC, SIZE_DESC -> SortCategory.SIZE
    }

    companion object {
        // Migration helper for old SortOption enum
        fun fromLegacy(legacy: String): SortOption = when (legacy) {
            "NAME" -> NAME_ASC
            "DATE_ADDED" -> DATE_ADDED_DESC
            "DURATION" -> DURATION_ASC
            "SIZE" -> SIZE_ASC
            else -> NAME_ASC
        }
    }
}

enum class SortCategory {
    NAME,
    DATE_ADDED,
    DURATION,
    SIZE
}

// Helper object for sorting media items
object MediaSorter {
    fun sortAudioItems(items: List<AudioItem>, option: SortOption): List<AudioItem> {
        val sorted = when (option.getCategory()) {
            SortCategory.NAME -> items.sortedBy { it.title.lowercase() }
            SortCategory.DATE_ADDED -> items.sortedBy { it.dateAdded }
            SortCategory.DURATION -> items.sortedBy { it.duration }
            SortCategory.SIZE -> items.sortedBy { it.size }
        }
        return if (option.isAscending()) sorted else sorted.reversed()
    }

    fun sortVideoItems(items: List<VideoItem>, option: SortOption): List<VideoItem> {
        val sorted = when (option.getCategory()) {
            SortCategory.NAME -> items.sortedBy { it.title.lowercase() }
            SortCategory.DATE_ADDED -> items.sortedBy { it.dateAdded }
            SortCategory.DURATION -> items.sortedBy { it.duration }
            SortCategory.SIZE -> items.sortedBy { it.size }
        }
        return if (option.isAscending()) sorted else sorted.reversed()
    }
}

// Extension functions that delegate to the helper object
@JvmName("sortedAudioByOption")
fun List<AudioItem>.sortedByOption(option: SortOption): List<AudioItem> =
    MediaSorter.sortAudioItems(this, option)

@JvmName("sortedVideoByOption")
fun List<VideoItem>.sortedByOption(option: SortOption): List<VideoItem> =
    MediaSorter.sortVideoItems(this, option)

enum class ViewMode {
    LIST,
    GRID
}

// App Preferences
data class AppPreferences(
    val audioViewMode: ViewMode = ViewMode.LIST,
    val videoViewMode: ViewMode = ViewMode.LIST,
    val audioSortOption: SortOption = SortOption.NAME_ASC,
    val videoSortOption: SortOption = SortOption.NAME_ASC,
    val selectedTheme: AppTheme = AppTheme.DARK,
    val lastPlaybackState: PlaybackState = PlaybackState(),
    val recentlyPlayedVideos: List<RecentlyPlayedVideo> = emptyList(),
    // *** NEW: Store maps of saved positions ***
    val audioPositions: Map<Long, Long> = emptyMap(),
    val videoPositions: Map<Long, Long> = emptyMap()
)

enum class AppTheme {
    LIGHT,
    DARK,
    AMOLED,
    CUSTOM
}

enum class AudioLibraryTab(val title: String) {
    LIBRARY("All Songs"),
    PLAYLISTS("Playlists")
}