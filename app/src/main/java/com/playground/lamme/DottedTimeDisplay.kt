package com.playground.lamme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DottedTimeDisplay(
    modifier: Modifier = Modifier,
    dotColor: Color = Color.White,
    dotSize: Float = 8f,
    spacing: Float = 16f
) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = getCurrentTime()
        }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            // Draw time in dotted format
            val timeString = currentTime
            val charWidth = 5 * spacing
            val totalWidth = timeString.length * charWidth
            var xOffset = centerX - totalWidth / 2

            timeString.forEach { char ->
                drawDottedCharacter(
                    char = char,
                    startX = xOffset,
                    startY = centerY - 40f,
                    dotSize = dotSize,
                    spacing = spacing,
                    color = dotColor
                )
                xOffset += charWidth
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDottedCharacter(
    char: Char,
    startX: Float,
    startY: Float,
    dotSize: Float,
    spacing: Float,
    color: Color
) {
    val dots = getDotPattern(char)
    
    dots.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { colIndex, shouldDraw ->
            if (shouldDraw) {
                drawCircle(
                    color = color,
                    radius = dotSize / 2,
                    center = Offset(
                        startX + colIndex * spacing,
                        startY + rowIndex * spacing
                    )
                )
            }
        }
    }
}

// Dot matrix patterns for each character (5x7 grid)
private fun getDotPattern(char: Char): List<List<Boolean>> {
    return when (char) {
        '0' -> listOf(
            listOf(false, true, true, true, false),
            listOf(true, false, false, false, true),
            listOf(true, false, false, true, true),
            listOf(true, false, true, false, true),
            listOf(true, true, false, false, true),
            listOf(true, false, false, false, true),
            listOf(false, true, true, true, false)
        )
        '1' -> listOf(
            listOf(false, false, true, false, false),
            listOf(false, true, true, false, false),
            listOf(false, false, true, false, false),
            listOf(false, false, true, false, false),
            listOf(false, false, true, false, false),
            listOf(false, false, true, false, false),
            listOf(false, true, true, true, false)
        )
        '2' -> listOf(
            listOf(false, true, true, true, false),
            listOf(true, false, false, false, true),
            listOf(false, false, false, false, true),
            listOf(false, false, false, true, false),
            listOf(false, false, true, false, false),
            listOf(false, true, false, false, false),
            listOf(true, true, true, true, true)
        )
        '3' -> listOf(
            listOf(false, true, true, true, false),
            listOf(true, false, false, false, true),
            listOf(false, false, false, false, true),
            listOf(false, false, true, true, false),
            listOf(false, false, false, false, true),
            listOf(true, false, false, false, true),
            listOf(false, true, true, true, false)
        )
        '4' -> listOf(
            listOf(false, false, false, true, false),
            listOf(false, false, true, true, false),
            listOf(false, true, false, true, false),
            listOf(true, false, false, true, false),
            listOf(true, true, true, true, true),
            listOf(false, false, false, true, false),
            listOf(false, false, false, true, false)
        )
        '5' -> listOf(
            listOf(true, true, true, true, true),
            listOf(true, false, false, false, false),
            listOf(true, true, true, true, false),
            listOf(false, false, false, false, true),
            listOf(false, false, false, false, true),
            listOf(true, false, false, false, true),
            listOf(false, true, true, true, false)
        )
        '6' -> listOf(
            listOf(false, false, true, true, false),
            listOf(false, true, false, false, false),
            listOf(true, false, false, false, false),
            listOf(true, true, true, true, false),
            listOf(true, false, false, false, true),
            listOf(true, false, false, false, true),
            listOf(false, true, true, true, false)
        )
        '7' -> listOf(
            listOf(true, true, true, true, true),
            listOf(false, false, false, false, true),
            listOf(false, false, false, true, false),
            listOf(false, false, true, false, false),
            listOf(false, true, false, false, false),
            listOf(false, true, false, false, false),
            listOf(false, true, false, false, false)
        )
        '8' -> listOf(
            listOf(false, true, true, true, false),
            listOf(true, false, false, false, true),
            listOf(true, false, false, false, true),
            listOf(false, true, true, true, false),
            listOf(true, false, false, false, true),
            listOf(true, false, false, false, true),
            listOf(false, true, true, true, false)
        )
        '9' -> listOf(
            listOf(false, true, true, true, false),
            listOf(true, false, false, false, true),
            listOf(true, false, false, false, true),
            listOf(false, true, true, true, true),
            listOf(false, false, false, false, true),
            listOf(false, false, false, true, false),
            listOf(false, true, true, false, false)
        )
        ':' -> listOf(
            listOf(false, false, false, false, false),
            listOf(false, false, true, false, false),
            listOf(false, false, true, false, false),
            listOf(false, false, false, false, false),
            listOf(false, false, true, false, false),
            listOf(false, false, true, false, false),
            listOf(false, false, false, false, false)
        )
        else -> List(7) { List(5) { false } }
    }
}

private fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}