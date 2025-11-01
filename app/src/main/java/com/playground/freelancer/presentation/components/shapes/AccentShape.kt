package com.playground.freelancer.presentation.components.shapes

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

fun DrawScope.drawAccentShape(color: androidx.compose.ui.graphics.Color) {
    val width = size.width
    val height = size.height * 0.7f // Shape covers 70% of the Box height

    val path = Path().apply {
        moveTo(0f, 0f)
        lineTo(width * 0.1f, 0f) // Start point
        quadraticBezierTo(
            x1 = width * 0.1f, y1 = height * 0.4f,
            x2 = width * 0.25f, y2 = height * 0.6f
        )
        quadraticBezierTo(
            x1 = width * 0.4f, y1 = height * 0.9f,
            x2 = width * 0.1f, y2 = height
        )
        lineTo(0f, height)
        close()
    }
    drawPath(path, color)
}
