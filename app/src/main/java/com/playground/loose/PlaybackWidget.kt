package com.playground.loose

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.RemoteViews
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.playground.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.graphics.scale
import androidx.core.net.toUri

/**
 * Singleton object to update all widgets from the service
 */

// FIX: Updated PlaybackWidget object to use service intents
object PlaybackWidget {
    @OptIn(UnstableApi::class)
    fun updateWidgets(
        context: Context,
        mediaId: Long,
        title: String,
        artist: String,
        albumArtUri: String?,
        isPlaying: Boolean
    ) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, PlaybackWidgetReceiver::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_playback)

            views.setTextViewText(R.id.widget_title, title)
            views.setTextViewText(R.id.widget_artist, artist)

            views.setImageViewResource(
                R.id.widget_play_pause,
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )

            if (albumArtUri != null) {
                loadAlbumArtAsync(context, albumArtUri, views, appWidgetManager, appWidgetId)
            } else {
                views.setImageViewResource(R.id.widget_album_art, R.drawable.ic_music_note)
            }

            // FIX: Use service intents instead of broadcast
            views.setOnClickPendingIntent(
                R.id.widget_play_pause,
                getServicePendingIntent(context, MediaPlaybackService.ACTION_PLAY_PAUSE)
            )
            views.setOnClickPendingIntent(
                R.id.widget_previous,
                getServicePendingIntent(context, MediaPlaybackService.ACTION_PREV)
            )
            views.setOnClickPendingIntent(
                R.id.widget_next,
                getServicePendingIntent(context, MediaPlaybackService.ACTION_NEXT)
            )

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
    }

    private fun loadAlbumArtAsync(
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
            }
        }
    }

    private fun loadBitmap(context: Context, uriString: String): Bitmap? {
        return try {
            val uri = uriString.toUri()

            if (uri.scheme == "content" || uri.scheme == "file") {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.let { bitmap ->
                        scaleBitmap(bitmap, 200)
                    }
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

    // FIX: Create service intent helper
    @UnstableApi
    private fun getServicePendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, MediaPlaybackService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}