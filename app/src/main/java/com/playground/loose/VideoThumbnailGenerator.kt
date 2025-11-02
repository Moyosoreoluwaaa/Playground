package com.playground.loose

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Efficient video thumbnail generator with disk and memory caching
 */
class VideoThumbnailGenerator(private val context: Context) {
    
    private val cacheDir = File(context.cacheDir, "video_thumbnails").apply {
        if (!exists()) mkdirs()
    }
    
    // Memory cache - store up to 50MB of bitmaps
    private val memoryCache = object : LruCache<Long, Bitmap>(50 * 1024 * 1024) {
        override fun sizeOf(key: Long, bitmap: Bitmap): Int {
            return bitmap.byteCount
        }
    }
    
    /**
     * Get thumbnail with caching (memory -> disk -> generate)
     */
    suspend fun getThumbnailUri(videoId: Long, videoUri: Uri): Uri? = withContext(Dispatchers.IO) {
        try {
            // Check memory cache first
            val cached = memoryCache.get(videoId)
            if (cached != null) {
                return@withContext getCachedThumbnailUri(videoId)
            }
            
            // Check disk cache
            val cachedFile = File(cacheDir, "$videoId.jpg")
            if (cachedFile.exists()) {
                return@withContext Uri.fromFile(cachedFile)
            }
            
            // Generate and cache thumbnail
            generateAndCacheThumbnail(videoId, videoUri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun generateAndCacheThumbnail(videoId: Long, videoUri: Uri): Uri? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, videoUri)
            
            // Get frame at 1 second or first frame
            val bitmap = retriever.getFrameAtTime(
                1_000_000, // 1 second in microseconds
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            ) ?: retriever.frameAtTime
            
            if (bitmap != null) {
                // Resize to reasonable size (480p wide)
                val scaledBitmap = scaleBitmap(bitmap, 480)
                
                // Save to disk cache
                val cacheFile = File(cacheDir, "$videoId.jpg")
                FileOutputStream(cacheFile).use { out ->
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }
                
                // Add to memory cache
                memoryCache.put(videoId, scaledBitmap)
                
                if (bitmap != scaledBitmap) {
                    bitmap.recycle()
                }
                
                return Uri.fromFile(cacheFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
    
    private fun getCachedThumbnailUri(videoId: Long): Uri {
        return Uri.fromFile(File(cacheDir, "$videoId.jpg"))
    }
    
    private fun scaleBitmap(bitmap: Bitmap, targetWidth: Int): Bitmap {
        val ratio = targetWidth.toFloat() / bitmap.width
        val targetHeight = (bitmap.height * ratio).toInt()
        
        return if (bitmap.width > targetWidth) {
            Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
        } else {
            bitmap
        }
    }
    
    /**
     * Pre-cache thumbnails in background for smooth scrolling
     */
    suspend fun preCacheThumbnails(videos: List<VideoItem>, limit: Int = 20) {
        withContext(Dispatchers.IO) {
            videos.take(limit).forEach { video ->
                // Only generate if not cached
                val cachedFile = File(cacheDir, "${video.id}.jpg")
                if (!cachedFile.exists() && memoryCache.get(video.id) == null) {
                    getThumbnailUri(video.id, video.uri)
                }
            }
        }
    }
    
    fun clearCache() {
        memoryCache.evictAll()
        cacheDir.listFiles()?.forEach { it.delete() }
    }
}