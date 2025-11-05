package com.playground.loose

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
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
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
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

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(MediaSessionCallback())
            .setSessionActivity(createSessionActivityIntent())
            .build()

        setupPlayerListener()
    }

    // MediaPlaybackService.kt - Update onStartCommand
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
            ACTION_NEXT -> {
                Log.d("MediaService", "⏭️ ACTION_NEXT received")
                mediaSession?.player?.let { player ->
                    when {
                        player.hasNextMediaItem() -> {
                            player.seekToNextMediaItem()
                        }
                        player.repeatMode == Player.REPEAT_MODE_ALL && player.mediaItemCount > 0 -> {
                            // Loop back to first item
                            player.seekTo(0, 0)
                            player.play()
                        }
                        player.mediaItemCount > 0 -> {
                            // No repeat mode, loop anyway for better UX
                            player.seekTo(0, 0)
                            player.play()
                        }
                    }
                }
            }
            ACTION_PREV -> {
                Log.d("MediaService", "⏮️ ACTION_PREV received")
                mediaSession?.player?.let { player ->
                    when {
                        player.currentPosition > 3000 -> {
                            // Restart current track if > 3 seconds
                            player.seekTo(0)
                        }
                        player.hasPreviousMediaItem() -> {
                            player.seekToPreviousMediaItem()
                        }
                        player.repeatMode == Player.REPEAT_MODE_ALL && player.mediaItemCount > 0 -> {
                            // Loop to last item
                            player.seekTo(player.mediaItemCount - 1, 0)
                            player.play()
                        }
                        player.mediaItemCount > 0 -> {
                            // No repeat mode, loop anyway for better UX
                            player.seekTo(player.mediaItemCount - 1, 0)
                            player.play()
                        }
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createSessionActivityIntent(): PendingIntent {
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
                updateAllWidgets()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateAllWidgets()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateAllWidgets()
            }
        })
    }

    private fun updateAllWidgets() {
        val player = mediaSession?.player ?: return
        val mediaItem = player.currentMediaItem ?: return
        val metadata = mediaItem.mediaMetadata

        val title = metadata.title?.toString() ?: "Unknown Title"
        val artist = metadata.artist?.toString() ?: "Unknown Artist"
        val artUri = mediaItem.localConfiguration?.uri?.toString()
            ?: metadata.artworkUri?.toString() ?: ""
        val isPlaying = player.isPlaying
        val mediaId = mediaItem.mediaId.toLongOrNull() ?: 0L

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

    // Update MediaSessionCallback
    private inner class MediaSessionCallback : MediaSession.Callback {

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            val resolved = mediaItems.map { item ->
                if (item.localConfiguration?.uri != null) {
                    item
                } else {
                    item.buildUpon()
                        .setUri(item.requestMetadata.mediaUri)
                        .build()
                }
            }.toMutableList()
            return Futures.immediateFuture(resolved)
        }

        override fun onMediaButtonEvent(
            session: MediaSession,
            controllerInfo: MediaSession.ControllerInfo,
            intent: Intent
        ): Boolean {
            val keyEvent = intent.getParcelableExtra<android.view.KeyEvent>(Intent.EXTRA_KEY_EVENT)

            if (keyEvent?.action == android.view.KeyEvent.ACTION_DOWN) {
                when (keyEvent.keyCode) {
                    android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                        if (player.isPlaying) player.pause() else player.play()
                        return true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_PLAY -> {
                        player.play()
                        return true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                        player.pause()
                        return true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_NEXT -> {
                        when {
                            player.hasNextMediaItem() -> player.seekToNextMediaItem()
                            player.repeatMode == Player.REPEAT_MODE_ALL && player.mediaItemCount > 0 -> {
                                player.seekTo(0, 0)
                                player.play()
                            }
                            player.mediaItemCount > 0 -> {
                                player.seekTo(0, 0)
                                player.play()
                            }
                        }
                        return true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                        when {
                            player.currentPosition > 3000 -> player.seekTo(0)
                            player.hasPreviousMediaItem() -> player.seekToPreviousMediaItem()
                            player.repeatMode == Player.REPEAT_MODE_ALL && player.mediaItemCount > 0 -> {
                                player.seekTo(player.mediaItemCount - 1, 0)
                                player.play()
                            }
                            player.mediaItemCount > 0 -> {
                                player.seekTo(player.mediaItemCount - 1, 0)
                                player.play()
                            }
                        }
                        return true
                    }
                }
            }

            return super.onMediaButtonEvent(session, controllerInfo, intent)
        }
    }
}