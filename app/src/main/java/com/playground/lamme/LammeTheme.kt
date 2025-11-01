package com.playground.lamme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Nothing OS inspired color palette
object LammeColors {
    val background = Color(0xFF000000)
    val primary = Color(0xFFFFFFFF)
    val secondary = Color(0xFF808080)
    val accent = Color(0xFFFF0000)
    val gridLine = Color(0xFF1A1A1A)
}

private val DarkColorScheme = darkColorScheme(
    primary = LammeColors.primary,
    secondary = LammeColors.secondary,
    background = LammeColors.background,
    surface = LammeColors.background
)

@Composable
fun LammeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}