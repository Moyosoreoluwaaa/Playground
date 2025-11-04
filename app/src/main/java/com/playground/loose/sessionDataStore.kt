package com.playground.loose

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "playback_session")

/**
 * Manages playback session state with proper restoration
 * Saves position every time playback pauses or position changes significantly
 */
class SessionManager(private val context: Context) {

    companion object {
        private const val TAG = "SessionManager"

        // Session keys
        private val KEY_MEDIA_ID = longPreferencesKey("session_media_id")
        private val KEY_POSITION = longPreferencesKey("session_position")
        private val KEY_IS_AUDIO_MODE = stringPreferencesKey("session_is_audio")
        private val KEY_LAST_SAVE_TIME = longPreferencesKey("session_last_save")

        // Save threshold - only save if position changed by more than this
        private const val SAVE_THRESHOLD_MS = 5000L // 5 seconds
    }

    private var lastSavedPosition = 0L
    private var lastSavedMediaId = 0L

    /**
     * Save current playback session (with throttling)
     */
    suspend fun saveSession(
        mediaId: Long,
        position: Long,
        isAudioMode: Boolean,
        force: Boolean = false
    ) {
        try {
            // Reset tracking when media changes
            if (mediaId != lastSavedMediaId) {
                lastSavedPosition = 0L
                lastSavedMediaId = mediaId
                Log.d(TAG, "📝 Media changed to $mediaId, reset tracking")
            }

            // -----------------------------------------------------------------
            // Prevent saving near-0 position if a valid position already exists
            // -----------------------------------------------------------------
            if (position < SAVE_THRESHOLD_MS && lastSavedPosition > SAVE_THRESHOLD_MS) {
                Log.w(
                    TAG,
                    "⚠️ Ignored session save near 0ms — last valid=${formatTime(lastSavedPosition)} " +
                            "for mediaId=$mediaId"
                )
                return
            }

            // Skip if position hasn't changed significantly (unless forced)
            if (!force &&
                mediaId == lastSavedMediaId &&
                kotlin.math.abs(position - lastSavedPosition) < SAVE_THRESHOLD_MS
            ) {
                return
            }

            context.sessionDataStore.edit { prefs ->
                prefs[KEY_MEDIA_ID] = mediaId
                prefs[KEY_POSITION] = position
                prefs[KEY_IS_AUDIO_MODE] = isAudioMode.toString()
                prefs[KEY_LAST_SAVE_TIME] = System.currentTimeMillis()
            }

            lastSavedPosition = position
            lastSavedMediaId = mediaId

            Log.d(TAG, "✅ Session saved: mediaId=$mediaId, position=${formatTime(position)}, isAudio=$isAudioMode")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to save session", e)
        }
    }


    /**
     * Get saved session
     */
    suspend fun getSession(): SavedSession? {
        return try {
            val session = context.sessionDataStore.data.map { prefs ->
                val mediaId = prefs[KEY_MEDIA_ID] ?: 0L
                val position = prefs[KEY_POSITION] ?: 0L
                val isAudioMode = prefs[KEY_IS_AUDIO_MODE]?.toBoolean() ?: true
                val lastSaveTime = prefs[KEY_LAST_SAVE_TIME] ?: 0L

                if (mediaId == 0L) {
                    null
                } else {
                    SavedSession(
                        mediaId = mediaId,
                        position = position,
                        isAudioMode = isAudioMode,
                        lastSaveTime = lastSaveTime
                    )
                }
            }.first()

            session?.let {
                Log.d(TAG, "📂 Loaded session: mediaId=${it.mediaId}, position=${formatTime(it.position)}, isAudio=${it.isAudioMode}")
            } ?: Log.d(TAG, "📂 No saved session found")

            session
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to get session", e)
            null
        }
    }

    /**
     * Clear session
     */
    suspend fun clearSession() {
        try {
            context.sessionDataStore.edit { prefs ->
                prefs.clear()
            }
            lastSavedPosition = 0L
            lastSavedMediaId = 0L
            Log.d(TAG, "🗑️ Session cleared")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to clear session", e)
        }
    }

    private fun formatTime(ms: Long): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val secs = seconds % 60
        return "${minutes}:${secs.toString().padStart(2, '0')}"
    }

    data class SavedSession(
        val mediaId: Long,
        val position: Long,
        val isAudioMode: Boolean,
        val lastSaveTime: Long
    )
}