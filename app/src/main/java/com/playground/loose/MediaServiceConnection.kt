//package com.playground.loose
//
//import android.content.ComponentName
//import android.content.Context
//import androidx.annotation.OptIn
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.session.MediaController
//import androidx.media3.session.SessionToken
//import com.google.common.util.concurrent.ListenableFuture
//import com.google.common.util.concurrent.MoreExecutors
//import kotlinx.coroutines.suspendCancellableCoroutine
//import kotlin.coroutines.resume
//import kotlin.coroutines.resumeWithException
//
///**
// * Helper class to manage MediaController connection to MediaPlaybackService
// */
//class MediaServiceConnection(private val context: Context) {
//
//    private var controllerFuture: ListenableFuture<MediaController>? = null
//    var mediaController: MediaController? = null
//        private set
//
//    @OptIn(UnstableApi::class)
//    suspend fun connect(): MediaController = suspendCancellableCoroutine { continuation ->
//        val sessionToken = SessionToken(
//            context,
//            ComponentName(context, MediaPlaybackService::class.java)
//        )
//
//        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
//
//        controllerFuture?.addListener(
//            {
//                try {
//                    mediaController = controllerFuture?.get()
//                    mediaController?.let {
//                        continuation.resume(it)
//                    } ?: continuation.resumeWithException(
//                        IllegalStateException("Failed to get MediaController")
//                    )
//                } catch (e: Exception) {
//                    continuation.resumeWithException(e)
//                }
//            },
//            MoreExecutors.directExecutor()
//        )
//
//        continuation.invokeOnCancellation {
//            disconnect()
//        }
//    }
//
//    fun disconnect() {
//        mediaController?.release()
//        MediaController.releaseFuture(controllerFuture ?: return)
//        mediaController = null
//        controllerFuture = null
//    }
//}