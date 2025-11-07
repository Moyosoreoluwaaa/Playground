package com.playground.loose

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "loose_preferences")

class PreferencesManager(private val context: Context) {

    private val gson: Gson = Gson()
    private object Keys {
        val MEDIA_ID = longPreferencesKey("media_id")
        val POSITION = longPreferencesKey("position")
        val IS_PLAYING = booleanPreferencesKey("is_playing")
        val REPEAT_MODE = stringPreferencesKey("repeat_mode")
        val IS_AUDIO_MODE = booleanPreferencesKey("is_audio_mode")
        val PLAYLIST_IDS = stringPreferencesKey("playlist_ids")
        val CURRENT_INDEX = intPreferencesKey("current_index")

        val AUDIO_VIEW_MODE = stringPreferencesKey("audio_view_mode")
        val VIDEO_VIEW_MODE = stringPreferencesKey("video_view_mode")
        val AUDIO_SORT = stringPreferencesKey("audio_sort")
        val VIDEO_SORT = stringPreferencesKey("video_sort")
        val THEME = stringPreferencesKey("theme")

        val RECENTLY_PLAYED_VIDEOS = stringPreferencesKey("recently_played_videos")
        val AUDIO_POSITIONS = stringPreferencesKey("audio_positions")
        val VIDEO_POSITIONS = stringPreferencesKey("video_positions")
        val AUDIO_PLAYLISTS = stringPreferencesKey("audio_playlists")
    }

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

        val audioPositions = parsePositions(prefs[Keys.AUDIO_POSITIONS])
        val videoPositions = parsePositions(prefs[Keys.VIDEO_POSITIONS])

        // Migration: convert old sort options to new format
        val audioSortString = prefs[Keys.AUDIO_SORT] ?: "NAME_ASC"
        val videoSortString = prefs[Keys.VIDEO_SORT] ?: "NAME_ASC"

        val audioSort = try {
            SortOption.valueOf(audioSortString)
        } catch (e: IllegalArgumentException) {
            // Migrate from old format
            SortOption.fromLegacy(audioSortString)
        }

        val videoSort = try {
            SortOption.valueOf(videoSortString)
        } catch (e: IllegalArgumentException) {
            // Migrate from old format
            SortOption.fromLegacy(videoSortString)
        }

        AppPreferences(
            audioViewMode = ViewMode.valueOf(
                prefs[Keys.AUDIO_VIEW_MODE] ?: ViewMode.LIST.name
            ),
            videoViewMode = ViewMode.valueOf(
                prefs[Keys.VIDEO_VIEW_MODE] ?: ViewMode.LIST.name
            ),
            audioSortOption = audioSort,
            videoSortOption = videoSort,
            selectedTheme = AppTheme.valueOf(
                prefs[Keys.THEME] ?: AppTheme.DARK.name
            ),
            lastPlaybackState = playbackState,
            recentlyPlayedVideos = recentlyPlayed,
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

            current.removeAll { it.first == mediaId }

            val posToSave = if (position < 1000) 0L else position
            current.add(0, mediaId to posToSave)

            val updated = current.take(100)
            prefs[key] = updated.joinToString(",") { "${it.first}:${it.second}" }
        }
    }

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

            current.removeAll { it.videoId == videoId }
            current.add(0, RecentlyPlayedVideo(videoId, System.currentTimeMillis()))

            val updated = current.take(10)
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

    val audioPlaylists: Flow<List<AudioPlaylist>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[Keys.AUDIO_PLAYLISTS] ?: "[]"
            try {
                val type = object : TypeToken<List<AudioPlaylist>>() {}.type
                gson.fromJson<List<AudioPlaylist>>(json, type) ?: emptyList()
            } catch (e: Exception) {
                Log.e("PrefsManager", "Error parsing playlists JSON: ${e.message}")
                emptyList()
            }
        }

    suspend fun createPlaylist(name: String, audioIds: List<Long>) {
        context.dataStore.edit { prefs ->
            val existingPlaylists = prefs[Keys.AUDIO_PLAYLISTS]?.let {
                val type = object : TypeToken<List<AudioPlaylist>>() {}.type
                gson.fromJson<List<AudioPlaylist>>(it, type)
            } ?: emptyList()

            val newPlaylist = AudioPlaylist(
                id = System.currentTimeMillis(),
                name = name,
                audioIds = audioIds,
                dateCreated = System.currentTimeMillis()
            )

            val updatedList = existingPlaylists + newPlaylist
            prefs[Keys.AUDIO_PLAYLISTS] = gson.toJson(updatedList)
        }
    }
}