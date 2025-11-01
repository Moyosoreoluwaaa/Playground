package com.playground.language.presentation.components


// Helper function to format time (0:45)
fun Long.toFormattedTime(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

