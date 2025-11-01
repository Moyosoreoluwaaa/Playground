package com.playground.freelancer.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CustomFreelancerDarkColorScheme = darkColorScheme(
    primary = Color(0xFFC2FF00), // Lime Green
    onPrimary = Color.Black,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White
)

@Composable
fun FreelancerAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CustomFreelancerDarkColorScheme,
        content = content
    )
}
