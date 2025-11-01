package com.playground.lamme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.sqrt

data class InteractiveCircle(
    val id: Int,
    val center: Offset,
    val baseRadius: Float,
    var color: Color = Color.White,
    var isActive: Boolean = false
)

@Composable
fun InteractiveCircles(
    modifier: Modifier = Modifier,
    circleCount: Int = 5,
    baseColor: Color = Color.White,
    activeColor: Color = Color.Red
) {
    var circles by remember { mutableStateOf<List<InteractiveCircle>>(emptyList()) }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    circles = circles.map { circle ->
                        val distance = sqrt(
                            (circle.center.x - tapOffset.x).pow(2) +
                            (circle.center.y - tapOffset.y).pow(2)
                        )
                        
                        if (distance <= circle.baseRadius) {
                            circle.copy(
                                isActive = !circle.isActive,
                                color = if (!circle.isActive) activeColor else baseColor
                            )
                        } else {
                            circle
                        }
                    }
                }
            }
    ) {
        // Initialize circles on first draw
        if (circles.isEmpty()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val maxRadius = size.minDimension / 2 - 40f
            
            circles = (0 until circleCount).map { index ->
                val radius = maxRadius * (circleCount - index) / circleCount
                InteractiveCircle(
                    id = index,
                    center = Offset(centerX, centerY),
                    baseRadius = radius,
                    color = baseColor.copy(alpha = 0.3f + (index * 0.1f))
                )
            }
        }

        // Draw all circles
        circles.sortedByDescending { it.baseRadius }.forEach { circle ->
            // Outer ring
            drawCircle(
                color = circle.color,
                radius = circle.baseRadius,
                center = circle.center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = if (circle.isActive) 4f else 2f
                )
            )

            // Inner fill if active
            if (circle.isActive) {
                drawCircle(
                    color = circle.color.copy(alpha = 0.1f),
                    radius = circle.baseRadius - 2f,
                    center = circle.center
                )
            }

            // Draw dots around the circle
            val dotCount = 8 + (circle.id * 4)
            for (i in 0 until dotCount) {
                val angle = (i * 360f / dotCount) * Math.PI / 180
                val dotX = circle.center.x + circle.baseRadius * kotlin.math.cos(angle).toFloat()
                val dotY = circle.center.y + circle.baseRadius * kotlin.math.sin(angle).toFloat()
                
                drawCircle(
                    color = circle.color,
                    radius = if (circle.isActive) 4f else 2f,
                    center = Offset(dotX, dotY)
                )
            }
        }

        // Center indicator
        drawCircle(
            color = if (circles.any { it.isActive }) activeColor else baseColor,
            radius = 6f,
            center = Offset(size.width / 2, size.height / 2)
        )
    }
}