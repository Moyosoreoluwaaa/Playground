package com.playground.lamme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class AODMode {
    TIME_AND_COMPASS,
    INTERACTIVE_CIRCLES,
    WATER_RIPPLE,
    EQUALIZER
}

@Composable
fun MainAODScreen() {
    var currentMode by remember { mutableStateOf(AODMode.EQUALIZER) }
    var brightness by remember { mutableFloatStateOf(0.5f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LammeColors.background)
    ) {
        // Background effect based on mode
        when (currentMode) {
            AODMode.TIME_AND_COMPASS -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    // Dotted time display
                    DottedTimeDisplay(
                        dotColor = LammeColors.primary.copy(alpha = brightness),
                        dotSize = 10f,
                        spacing = 18f
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Compass
                    CompassFeature(
                        modifier = Modifier.size(250.dp),
                        compassColor = LammeColors.primary.copy(alpha = brightness * 0.8f),
                        accentColor = LammeColors.accent
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            AODMode.INTERACTIVE_CIRCLES -> {
                InteractiveCircles(
                    modifier = Modifier.fillMaxSize(),
                    circleCount = 6,
                    baseColor = LammeColors.primary.copy(alpha = brightness * 0.8f),
                    activeColor = LammeColors.accent
                )
            }

            AODMode.WATER_RIPPLE -> {
                WaterRippleEffect(
                    modifier = Modifier.fillMaxSize(),
                    waterColor = Color.Cyan.copy(alpha = 0.2f * brightness),
                    rippleColor = LammeColors.primary.copy(alpha = brightness)
                )
            }

            AODMode.EQUALIZER -> {
                EqualizerEffect(
                    modifier = Modifier.fillMaxSize(),
                    barColor = LammeColors.primary.copy(alpha = brightness * 0.8f),
                    activeColor = LammeColors.accent,
                    barCount = 40
                )
            }
        }

        // Brightness control bar at bottom
        BrightnessBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            barColor = LammeColors.primary.copy(alpha = brightness),
            accentColor = LammeColors.accent,
            onBrightnessChange = { newBrightness ->
                brightness = newBrightness
            }
        )

        // Mode switcher dots at top
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AODMode.entries.forEach { mode ->
                ModeDot(
                    isActive = currentMode == mode,
                    onClick = { currentMode = mode },
                    color = if (currentMode == mode) LammeColors.accent else LammeColors.secondary
                )
            }
        }
    }
}

@Composable
fun ModeDot(
    isActive: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .size(12.dp)
            .padding(2.dp)
    ) {
        drawCircle(
            color = color,
            radius = if (isActive) size.minDimension / 2 else size.minDimension / 3
        )
    }
}