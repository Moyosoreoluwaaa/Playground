package com.playground.layer.presentation.theme

import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Custom Colors to match the UI image
val OrangeAccent = Color(0xFFF5A623) // Similar to the Review Swap button color
val DarkBackground = Color(0xFF000000) // Pure Black background
val CardOnBackground = Color(0xFF000000) // Black text on white card
val SearchFieldBackground = Color(0xFF333333) // Search bar background
val BrowserBackground = Color(0xFF1E1E1E) // Screen background

val SwapDarkColorScheme = darkColorScheme(
    primary = OrangeAccent,
    onPrimary = Color.Black,
    surface = DarkBackground,
    onSurface = Color.White,
    background = DarkBackground,
    onBackground = Color.White,
    surfaceVariant = Color(0xFF1E1E1E) // For smaller elements
)

val SwapTypography = Typography(
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
)

val SwapShapes = Shapes(
    extraLarge = RoundedCornerShape(16.dp), // For the large cards
    medium = RoundedCornerShape(8.dp) // For buttons
)

@Composable
fun SwapAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SwapDarkColorScheme,
        typography = SwapTypography,
        shapes = SwapShapes,
        content = content
    )
}