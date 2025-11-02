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
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "loose_preferences")

class PreferencesManager(private val context: Context) {

    private object Keys {
        // Playback State
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
            recentlyPlayedVideos = recentlyPlayed
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

//package com.playground.loose
//
//import android.content.Context
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.core.booleanPreferencesKey
//import androidx.datastore.preferences.core.edit
//import androidx.datastore.preferences.core.intPreferencesKey
//import androidx.datastore.preferences.core.longPreferencesKey
//import androidx.datastore.preferences.core.stringPreferencesKey
//import androidx.datastore.preferences.preferencesDataStore
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.map
//
//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "loose_preferences")
//
//class PreferencesManager(private val context: Context) {
//
//    private object Keys {
//        // Playback State
//        val MEDIA_ID = longPreferencesKey("media_id")
//        val POSITION = longPreferencesKey("position")
//        val IS_PLAYING = booleanPreferencesKey("is_playing")
//        val REPEAT_MODE = stringPreferencesKey("repeat_mode")
//        val IS_AUDIO_MODE = booleanPreferencesKey("is_audio_mode")
//        val PLAYLIST_IDS = stringPreferencesKey("playlist_ids")
//        val CURRENT_INDEX = intPreferencesKey("current_index")
//
//        // View Preferences
//        val AUDIO_VIEW_MODE = stringPreferencesKey("audio_view_mode")
//        val VIDEO_VIEW_MODE = stringPreferencesKey("video_view_mode")
//        val AUDIO_SORT = stringPreferencesKey("audio_sort")
//        val VIDEO_SORT = stringPreferencesKey("video_sort")
//        val THEME = stringPreferencesKey("theme")
//    }
//
//    val appPreferences: Flow<AppPreferences> = context.dataStore.data.map { prefs ->
//        val playbackState = PlaybackState(
//            mediaId = prefs[Keys.MEDIA_ID] ?: 0L,
//            position = prefs[Keys.POSITION] ?: 0L,
//            isPlaying = prefs[Keys.IS_PLAYING] ?: false,
//            repeatMode = RepeatMode.valueOf(
//                prefs[Keys.REPEAT_MODE] ?: RepeatMode.OFF.name
//            ),
//            isAudioMode = prefs[Keys.IS_AUDIO_MODE] ?: true,
//            currentPlaylistIds = prefs[Keys.PLAYLIST_IDS]?.split(",")
//                ?.mapNotNull { it.toLongOrNull() } ?: emptyList(),
//            currentIndex = prefs[Keys.CURRENT_INDEX] ?: 0
//        )
//
//        AppPreferences(
//            audioViewMode = ViewMode.valueOf(
//                prefs[Keys.AUDIO_VIEW_MODE] ?: ViewMode.LIST.name
//            ),
//            videoViewMode = ViewMode.valueOf(
//                prefs[Keys.VIDEO_VIEW_MODE] ?: ViewMode.LIST.name
//            ),
//            audioSortOption = SortOption.valueOf(
//                prefs[Keys.AUDIO_SORT] ?: SortOption.NAME.name
//            ),
//            videoSortOption = SortOption.valueOf(
//                prefs[Keys.VIDEO_SORT] ?: SortOption.NAME.name
//            ),
//            selectedTheme = AppTheme.valueOf(
//                prefs[Keys.THEME] ?: AppTheme.DARK.name
//            ),
//            lastPlaybackState = playbackState
//        )
//    }
//
//    suspend fun savePlaybackState(state: PlaybackState) {
//        context.dataStore.edit { prefs ->
//            prefs[Keys.MEDIA_ID] = state.mediaId
//            prefs[Keys.POSITION] = state.position
//            prefs[Keys.IS_PLAYING] = state.isPlaying
//            prefs[Keys.REPEAT_MODE] = state.repeatMode.name
//            prefs[Keys.IS_AUDIO_MODE] = state.isAudioMode
//            prefs[Keys.PLAYLIST_IDS] = state.currentPlaylistIds.joinToString(",")
//            prefs[Keys.CURRENT_INDEX] = state.currentIndex
//        }
//    }
//
//    suspend fun saveAudioViewMode(mode: ViewMode) {
//        context.dataStore.edit { it[Keys.AUDIO_VIEW_MODE] = mode.name }
//    }
//
//    suspend fun saveVideoViewMode(mode: ViewMode) {
//        context.dataStore.edit { it[Keys.VIDEO_VIEW_MODE] = mode.name }
//    }
//
//    suspend fun saveAudioSort(sort: SortOption) {
//        context.dataStore.edit { it[Keys.AUDIO_SORT] = sort.name }
//    }
//
//    suspend fun saveVideoSort(sort: SortOption) {
//        context.dataStore.edit { it[Keys.VIDEO_SORT] = sort.name }
//    }
//
//    suspend fun saveTheme(theme: AppTheme) {
//        context.dataStore.edit { it[Keys.THEME] = theme.name }
//    }
//
//    suspend fun clearPlaybackState() {
//        context.dataStore.edit { prefs ->
//            prefs[Keys.MEDIA_ID] = 0L
//            prefs[Keys.POSITION] = 0L
//            prefs[Keys.IS_PLAYING] = false
//            prefs[Keys.CURRENT_INDEX] = 0
//        }
//    }
//}