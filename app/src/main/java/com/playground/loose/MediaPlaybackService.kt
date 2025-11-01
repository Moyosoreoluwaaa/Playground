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

@UnstableApi
class MediaPlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private val channelId = "loose_media_playback"
    private val notificationId = 1001

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

        // Setup player listener for notification updates
        setupPlayerListener()
    }

    private fun createSessionActivityIntent(): PendingIntent {
        // Create intent to launch your main activity
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
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
                // Update notification based on playback state
                when (playbackState) {
                    Player.STATE_READY, Player.STATE_BUFFERING -> {
                        // Keep service in foreground when playing or buffering
                    }
                    Player.STATE_IDLE, Player.STATE_ENDED -> {
                        // Can stop foreground when idle
                        if (!player.playWhenReady) {
                            stopForeground(STOP_FOREGROUND_DETACH)
                        }
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // Notification automatically updates via MediaSession
            }
        })
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
                // Enable lock screen controls
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Stop playback when app is removed from recent apps
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
            // FIXED: Keep the original URI from the request instead of replacing with mediaId
            // The mediaItem already has the correct content:// URI set in PlayerViewModel
            val resolved = mediaItems.map { item ->
                // If the item already has a URI, use it. Otherwise try to construct from mediaId
                if (item.localConfiguration?.uri != null) {
                    item // Already has URI, return as-is
                } else {
                    // Fallback: this shouldn't happen with your current setup
                    item.buildUpon()
                        .setUri(item.requestMetadata.mediaUri)
                        .build()
                }
            }.toMutableList()
            return Futures.immediateFuture(resolved)
        }

        // Handle custom commands if needed
        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: androidx.media3.session.SessionCommand,
            args: android.os.Bundle
        ): ListenableFuture<androidx.media3.session.SessionResult> {
            return super.onCustomCommand(session, controller, customCommand, args)
        }
    }
}