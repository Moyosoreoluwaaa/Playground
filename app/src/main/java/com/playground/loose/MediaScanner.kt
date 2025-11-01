package com.playground.loose

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaScanner(private val context: Context) {

    suspend fun scanAudioFiles(sortOption: SortOption = SortOption.NAME): List<AudioItem> =
        withContext(Dispatchers.IO) {
            val audioList = mutableListOf<AudioItem>()
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID
            )

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val sortOrder = when (sortOption) {
                SortOption.NAME -> "${MediaStore.Audio.Media.TITLE} ASC"
                SortOption.DATE_ADDED -> "${MediaStore.Audio.Media.DATE_ADDED} DESC"
                SortOption.DURATION -> "${MediaStore.Audio.Media.DURATION} DESC"
                SortOption.SIZE -> "${MediaStore.Audio.Media.SIZE} DESC"
            }

            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val albumId = cursor.getLong(albumIdColumn)
                    val uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    )

                    audioList.add(
                        AudioItem(
                            id = id,
                            uri = uri,
                            title = cursor.getString(titleColumn) ?: "Unknown",
                            artist = cursor.getString(artistColumn),
                            album = cursor.getString(albumColumn),
                            duration = cursor.getLong(durationColumn),
                            size = cursor.getLong(sizeColumn),
                            dateAdded = cursor.getLong(dateColumn),
                            albumArtUri = albumArtUri,
                            path = cursor.getString(dataColumn)
                        )
                    )
                }
            }
            audioList
        }

    suspend fun scanVideoFiles(sortOption: SortOption = SortOption.NAME): List<VideoItem> =
        withContext(Dispatchers.IO) {
            val videoList = mutableListOf<VideoItem>()
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT
            )

            val sortOrder = when (sortOption) {
                SortOption.NAME -> "${MediaStore.Video.Media.TITLE} ASC"
                SortOption.DATE_ADDED -> "${MediaStore.Video.Media.DATE_ADDED} DESC"
                SortOption.DURATION -> "${MediaStore.Video.Media.DURATION} DESC"
                SortOption.SIZE -> "${MediaStore.Video.Media.SIZE} DESC"
            }

            context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val uri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    videoList.add(
                        VideoItem(
                            id = id,
                            uri = uri,
                            title = cursor.getString(titleColumn) ?: "Unknown",
                            duration = cursor.getLong(durationColumn),
                            size = cursor.getLong(sizeColumn),
                            dateAdded = cursor.getLong(dateColumn),
                            thumbnailUri = uri,
                            path = cursor.getString(dataColumn),
                            width = cursor.getInt(widthColumn),
                            height = cursor.getInt(heightColumn)
                        )
                    )
                }
            }
            videoList
        }
}