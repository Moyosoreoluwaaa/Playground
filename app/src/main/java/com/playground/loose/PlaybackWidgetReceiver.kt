package com.playground.loose

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.RemoteViews
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import com.playground.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.graphics.scale

class PlaybackWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    @OptIn(UnstableApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_PLAY_PAUSE -> {
                context.startService(
                    Intent(context, MediaPlaybackService::class.java).apply {
                        action = MediaPlaybackService.ACTION_PLAY_PAUSE
                    }
                )
            }
            ACTION_NEXT -> {
                context.startService(
                    Intent(context, MediaPlaybackService::class.java).apply {
                        action = MediaPlaybackService.ACTION_NEXT
                    }
                )
            }
            ACTION_PREVIOUS -> {
                context.startService(
                    Intent(context, MediaPlaybackService::class.java).apply {
                        action = MediaPlaybackService.ACTION_PREV
                    }
                )
            }
        }
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.playground.loose.widget.PLAY_PAUSE"
        const val ACTION_NEXT = "com.playground.loose.widget.NEXT"
        const val ACTION_PREVIOUS = "com.playground.loose.widget.PREVIOUS"

        private fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            title: String = "No track playing",
            artist: String = "Unknown Artist",
            albumArtUri: String? = null,
            isPlaying: Boolean = false
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_playback)

            // Set text
            views.setTextViewText(R.id.widget_title, title)
            views.setTextViewText(R.id.widget_artist, artist)

            // Set album art (default icon if no URI)
            if (albumArtUri != null) {
                // Load album art asynchronously
                loadAlbumArt(context, albumArtUri, views, appWidgetManager, appWidgetId)
            } else {
                views.setImageViewResource(R.id.widget_album_art, R.drawable.ic_music_note)
            }

            // Set play/pause icon
            views.setImageViewResource(
                R.id.widget_play_pause,
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )

            // Set up intents for buttons
            views.setOnClickPendingIntent(
                R.id.widget_play_pause,
                getPendingIntent(context, ACTION_PLAY_PAUSE)
            )
            views.setOnClickPendingIntent(
                R.id.widget_previous,
                getPendingIntent(context, ACTION_PREVIOUS)
            )
            views.setOnClickPendingIntent(
                R.id.widget_next,
                getPendingIntent(context, ACTION_NEXT)
            )

            // Click on widget opens app
            val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            val launchPendingIntent = PendingIntent.getActivity(
                context,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, launchPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun loadAlbumArt(
            context: Context,
            uriString: String,
            views: RemoteViews,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                try {
                    val bitmap = loadBitmap(context, uriString)
                    withContext(Dispatchers.Main) {
                        if (bitmap != null) {
                            views.setImageViewBitmap(R.id.widget_album_art, bitmap)
                        } else {
                            views.setImageViewResource(R.id.widget_album_art, R.drawable.ic_music_note)
                        }
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        views.setImageViewResource(R.id.widget_album_art, R.drawable.ic_music_note)
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                }
            }
        }

        private fun loadBitmap(context: Context, uriString: String): Bitmap? {
            return try {
                val uri = uriString.toUri()

                // Try loading from content resolver first (for local files)
                if (uri.scheme == "content" || uri.scheme == "file") {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)?.let { bitmap ->
                            // Scale down for widget
                            scaleBitmap(bitmap, 200)
                        }
                    }
                } else if (uri.scheme == "http" || uri.scheme == "https") {
                    // Load from network
                    val url = URL(uriString)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input = connection.inputStream
                    BitmapFactory.decodeStream(input)?.let { bitmap ->
                        scaleBitmap(bitmap, 200)
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
            val ratio = maxSize.toFloat() / maxOf(bitmap.width, bitmap.height)
            val width = (bitmap.width * ratio).toInt()
            val height = (bitmap.height * ratio).toInt()
            return bitmap.scale(width, height)
        }

        private fun getPendingIntent(context: Context, action: String): PendingIntent {
            val intent = Intent(context, PlaybackWidgetReceiver::class.java).apply {
                this.action = action
            }
            return PendingIntent.getBroadcast(
                context,
                action.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
