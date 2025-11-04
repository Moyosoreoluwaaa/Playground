package com.playground.loose

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "loose_preferences")

class PreferencesManager(private val context: Context) {

    private object Keys {
        // Playback State (Global)
        val MEDIA_ID = longPreferencesKey("media_id")
        val POSITION = longPreferencesKey("position")
        val IS_PLAYING = booleanPreferencesKey("is_playing")
        val REPEAT_MODE = stringPreferencesKey("repeat_mode")
        val IS_AUDIO_MODE = booleanPreferencesKey("is_audio_mode")
        val PLAYLIST_IDS = stringPreferencesKey("playlist_ids")
        val CURRENT_INDEX = intPreferencesKey("current_index")

        // View Preferences
        val AUDIO_VIEW_MODE = stringPreferencesKey("audio_view_mode")
        val VIDEO_VIEW_MODE = stringPreferencesKey("video_view_mode")
        val AUDIO_SORT = stringPreferencesKey("audio_sort")
        val VIDEO_SORT = stringPreferencesKey("video_sort")
        val THEME = stringPreferencesKey("theme")

        // Recently Played Videos (format: "videoId:timestamp,videoId:timestamp,...")
        val RECENTLY_PLAYED_VIDEOS = stringPreferencesKey("recently_played_videos")

        // *** NEW: Per-Media Position Storage ***
        // Format: "mediaId:position,mediaId:position,..."
        val AUDIO_POSITIONS = stringPreferencesKey("audio_positions")
        val VIDEO_POSITIONS = stringPreferencesKey("video_positions")
    }

    /**
     * Helper to parse "id:position,id:position" strings into a Map
     */
    private fun parsePositions(positionsString: String?): Map<Long, Long> {
        return positionsString?.split(",")
            ?.mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    val id = parts[0].toLongOrNull()
                    val pos = parts[1].toLongOrNull()
                    if (id != null && pos != null && pos > 0) id to pos else null
                } else null
            }?.toMap() ?: emptyMap()
    }

    val appPreferences: Flow<AppPreferences> = context.dataStore.data.map { prefs ->
        val playbackState = PlaybackState(
            mediaId = prefs[Keys.MEDIA_ID] ?: 0L,
            position = prefs[Keys.POSITION] ?: 0L,
            isPlaying = prefs[Keys.IS_PLAYING] ?: false,
            repeatMode = RepeatMode.valueOf(
                prefs[Keys.REPEAT_MODE] ?: RepeatMode.OFF.name
            ),
            isAudioMode = prefs[Keys.IS_AUDIO_MODE] ?: true,
            currentPlaylistIds = prefs[Keys.PLAYLIST_IDS]?.split(",")
                ?.mapNotNull { it.toLongOrNull() } ?: emptyList(),
            currentIndex = prefs[Keys.CURRENT_INDEX] ?: 0
        )

        // Parse recently played videos
        val recentlyPlayed = prefs[Keys.RECENTLY_PLAYED_VIDEOS]?.split(",")
            ?.mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    RecentlyPlayedVideo(
                        videoId = parts[0].toLongOrNull() ?: return@mapNotNull null,
                        playedAt = parts[1].toLongOrNull() ?: return@mapNotNull null
                    )
                } else null
            } ?: emptyList()

        // *** NEW: Parse per-media positions ***
        val audioPositions = parsePositions(prefs[Keys.AUDIO_POSITIONS])
        val videoPositions = parsePositions(prefs[Keys.VIDEO_POSITIONS])

        AppPreferences(
            audioViewMode = ViewMode.valueOf(
                prefs[Keys.AUDIO_VIEW_MODE] ?: ViewMode.LIST.name
            ),
            videoViewMode = ViewMode.valueOf(
                prefs[Keys.VIDEO_VIEW_MODE] ?: ViewMode.LIST.name
            ),
            audioSortOption = SortOption.valueOf(
                prefs[Keys.AUDIO_SORT] ?: SortOption.NAME.name
            ),
            videoSortOption = SortOption.valueOf(
                prefs[Keys.VIDEO_SORT] ?: SortOption.NAME.name
            ),
            selectedTheme = AppTheme.valueOf(
                prefs[Keys.THEME] ?: AppTheme.DARK.name
            ),
            lastPlaybackState = playbackState,
            recentlyPlayedVideos = recentlyPlayed,
            // *** NEW: Add to AppPreferences ***
            audioPositions = audioPositions,
            videoPositions = videoPositions
        )
    }

    suspend fun savePlaybackState(state: PlaybackState) {
        context.dataStore.edit { prefs ->
            prefs[Keys.MEDIA_ID] = state.mediaId
            prefs[Keys.POSITION] = state.position
            prefs[Keys.IS_PLAYING] = state.isPlaying
            prefs[Keys.REPEAT_MODE] = state.repeatMode.name
            prefs[Keys.IS_AUDIO_MODE] = state.isAudioMode
            prefs[Keys.PLAYLIST_IDS] = state.currentPlaylistIds.joinToString(",")
            prefs[Keys.CURRENT_INDEX] = state.currentIndex
        }
    }

    // *** NEW: Generic function to save a single position ***
    /**
     * Saves a position for a mediaId.
     * This uses an LRU (Least Recently Used) cache strategy, storing up to 100 positions.
     * Positions less than 1 second (1000ms) are saved as 0 (reset).
     */
    private suspend fun savePosition(
        key: Preferences.Key<String>,
        mediaId: Long,
        position: Long
    ) {
        context.dataStore.edit { prefs ->
            val current = prefs[key]?.split(",")
                ?.mapNotNull { entry ->
                    val parts = entry.split(":")
                    if (parts.size == 2) {
                        (parts[0].toLongOrNull() ?: return@mapNotNull null) to
                                (parts[1].toLongOrNull() ?: return@mapNotNull null)
                    } else null
                }?.toMutableList() ?: mutableListOf()

            // Find and remove existing entry for this mediaId
            current.removeAll { it.first == mediaId }

            // If position is < 1s, save 0 (reset). Otherwise, save the position.
            val posToSave = if (position < 1000) 0L else position

            // Add new entry to the front (LRU style)
            current.add(0, mediaId to posToSave)

            // Keep only the last 100 entries
            val updated = current.take(100)

            // Save back
            prefs[key] = updated.joinToString(",") { "${it.first}:${it.second}" }
        }
    }

    // *** NEW: Public functions to save positions ***
    suspend fun saveAudioPosition(mediaId: Long, position: Long) {
        savePosition(Keys.AUDIO_POSITIONS, mediaId, position)
    }

    suspend fun saveVideoPosition(mediaId: Long, position: Long) {
        savePosition(Keys.VIDEO_POSITIONS, mediaId, position)
    }


    suspend fun addRecentlyPlayedVideo(videoId: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.RECENTLY_PLAYED_VIDEOS]?.split(",")
                ?.mapNotNull { entry ->
                    val parts = entry.split(":")
                    if (parts.size == 2) {
                        RecentlyPlayedVideo(
                            videoId = parts[0].toLongOrNull() ?: return@mapNotNull null,
                            playedAt = parts[1].toLongOrNull() ?: return@mapNotNull null
                        )
                    } else null
                }?.toMutableList() ?: mutableListOf()

            // Remove if already exists
            current.removeAll { it.videoId == videoId }

            // Add to front with current timestamp
            current.add(0, RecentlyPlayedVideo(videoId, System.currentTimeMillis()))

            // Keep only last 10
            val updated = current.take(10)

            // Save back
            prefs[Keys.RECENTLY_PLAYED_VIDEOS] = updated.joinToString(",") {
                "${it.videoId}:${it.playedAt}"
            }
        }
    }

    suspend fun saveAudioViewMode(mode: ViewMode) {
        context.dataStore.edit { it[Keys.AUDIO_VIEW_MODE] = mode.name }
    }

    suspend fun saveVideoViewMode(mode: ViewMode) {
        context.dataStore.edit { it[Keys.VIDEO_VIEW_MODE] = mode.name }
    }

    suspend fun saveAudioSort(sort: SortOption) {
        context.dataStore.edit { it[Keys.AUDIO_SORT] = sort.name }
    }

    suspend fun saveVideoSort(sort: SortOption) {
        context.dataStore.edit { it[Keys.VIDEO_SORT] = sort.name }
    }

    // *** NEW: Dedicated function to save repeat mode ***
    suspend fun saveRepeatMode(mode: RepeatMode) {
        context.dataStore.edit { it[Keys.REPEAT_MODE] = mode.name }
    }

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { it[Keys.THEME] = theme.name }
    }

    suspend fun clearPlaybackState() {
        context.dataStore.edit { prefs ->
            prefs[Keys.MEDIA_ID] = 0L
            prefs[Keys.POSITION] = 0L
            prefs[Keys.IS_PLAYING] = false
            prefs[Keys.CURRENT_INDEX] = 0
        }
    }
}