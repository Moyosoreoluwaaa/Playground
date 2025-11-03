package com.playground.loose

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@UnstableApi
class MediaPlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private val channelId = "loose_media_playback"

    // Coroutine scope for widget updates
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        // Actions for widget and service intents
        const val ACTION_PLAY_PAUSE = "com.playground.loose.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.playground.loose.ACTION_NEXT"
        const val ACTION_PREV = "com.playground.loose.ACTION_PREV"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()

        // Create MediaSession with proper configuration
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(MediaSessionCallback())
            .setSessionActivity(createSessionActivityIntent())
            .build()

        // Setup player listener for notification AND WIDGET updates
        setupPlayerListener()
    }

    /**
     * Handle incoming intents from the widget.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> {
                mediaSession?.player?.let {
                    if (it.isPlaying) {
                        it.pause()
                    } else {
                        it.play()
                    }
                }
            }
            ACTION_NEXT -> mediaSession?.player?.seekToNextMediaItem()
            ACTION_PREV -> mediaSession?.player?.seekToPreviousMediaItem()
        }
        // Let the system know the service should continue running
        // in the background, as started by the base class.
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createSessionActivityIntent(): PendingIntent {
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            // Add a flag or extra to navigate to the player screen if needed
            // e.g., intent.putExtra("NAVIGATE_TO", "PLAYER")
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun setupPlayerListener() {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY, Player.STATE_BUFFERING -> {
                        // Keep service in foreground
                    }
                    Player.STATE_IDLE, Player.STATE_ENDED -> {
                        if (!player.playWhenReady) {
                            stopForeground(STOP_FOREGROUND_DETACH)
                        }
                    }
                }
                // Update widget on any state change
                updateAllWidgets()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // Update widget when play/pause state changes
                updateAllWidgets()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                // Update widget when track changes
                updateAllWidgets()
            }
        })
    }

    /**
     * Gathers current player state and triggers a widget update.
     */
    private fun updateAllWidgets() {
        val player = mediaSession?.player ?: return
        val mediaItem = player.currentMediaItem ?: return
        val metadata = mediaItem.mediaMetadata

        val title = metadata.title?.toString() ?: "Unknown Title"
        val artist = metadata.artist?.toString() ?: "Unknown Artist"
        // Use the URI from localConfiguration if available, as set in onAddMediaItems
        val artUri = mediaItem.localConfiguration?.uri?.toString()
            ?: metadata.artworkUri?.toString() ?: ""
        val isPlaying = player.isPlaying

        // The MediaItem ID might be the content URI string, or you might need
        // to get the ID from your ViewModel/Repository when you *add* the item.
        // For simplicity, we'll use a hashcode or 0.
        // A better approach is to have your ViewModel store the current AudioItem
        // and have the service read from it, or pass the AudioItem.id
        // via RequestMetadata when building the MediaItem.
        val mediaId = mediaItem.mediaId.toLongOrNull() ?: 0L // Assuming mediaId is the long ID

        // Update widgets on a background thread
        serviceScope.launch(Dispatchers.IO) {
            PlaybackWidget.updateWidgets(
                context = this@MediaPlaybackService,
                mediaId = mediaId,
                title = title,
                artist = artist,
                albumArtUri = artUri,
                isPlaying = isPlaying
            )
        }
    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Media playback controls"
                setShowBadge(false)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val stopPlayback = mediaSession?.player?.playWhenReady == false
        if (stopPlayback) {
            stopSelf()
        }
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }

    private inner class MediaSessionCallback : MediaSession.Callback {

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            // CRITICAL FIX: Keep the original URI instead of replacing with mediaId
            val resolved = mediaItems.map { item ->
                if (item.localConfiguration?.uri != null) {
                    // Already has URI from PlayerViewModel, use as-is
                    item
                } else {
                    // Fallback to using requestMetadata if localConfig is missing
                    item.buildUpon()
                        .setUri(item.requestMetadata.mediaUri)
                        .build()
                }
            }.toMutableList()
            return Futures.immediateFuture(resolved)
        }
    }
}