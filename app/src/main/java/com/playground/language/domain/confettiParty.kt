package com.playground.language.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

/**
 * Defines the configuration for a single, reusable confetti burst.
 */
val confettiParty: Party = Party(
    speed = 0f,
    maxSpeed = 30f,
    damping = 0.9f,
    angle = Angle.TOP,
    spread = 360,
    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def).map { Color(it).toArgb() },
    shapes = listOf(Shape.Square, Shape.Circle),
    size = listOf(Size.SMALL, Size.MEDIUM, Size.LARGE),
    // Emitter: 100 particles max, emitted over 100 milliseconds
    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
    // Position: Relative to the canvas center (0.5, 0.5)
    position = Position.Relative(0.5, 0.5)
)
