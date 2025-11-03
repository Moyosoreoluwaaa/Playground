package com.playground.loose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.palette.graphics.Palette
import java.io.InputStream

/**
 * Extracts a Palette from the given image Uri.
 */
fun extractPalette(context: Context, uriString: String): Palette? {
    return try {
        val uri = Uri.parse(uriString)
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        bitmap?.let { Palette.from(it).generate() }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Extension to safely get the dominant color from a Palette.
 */
fun Palette.getDominantColor(defaultColor: Int): Int {
    return this.getDominantColor(defaultColor)
}
