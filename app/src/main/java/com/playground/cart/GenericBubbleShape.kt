package com.playground.cart

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.geometry.RoundRect // Ensure this import is available
import androidx.compose.ui.unit.dp

/**
 * A simplified custom Shape for the speech bubble.
 */
class GenericBubbleShape(private val cornerRadius: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        with(density) { // Use with(density) to enable Dp.toPx()
            val cr = cornerRadius.toPx()
            val rect = Rect(0f, 0f, size.width, size.height * 0.8f)

            return Outline.Generic(
                Path().apply {
                    // Fix 1: Use the correct RoundRect constructor and pass corner radii individually
                    addRoundRect(
                        RoundRect(
                            rect = rect,
                            topLeft = androidx.compose.ui.geometry.CornerRadius(cr, cr),
                            topRight = androidx.compose.ui.geometry.CornerRadius(cr, cr),
                            bottomRight = androidx.compose.ui.geometry.CornerRadius(cr, cr),
                            bottomLeft = androidx.compose.ui.geometry.CornerRadius(cr, cr)
                        )
                    )

                    // Define the tail (small triangle pointing down)
                    val tailWidth = 10.dp.toPx()
                    val tailHeight = 15.dp.toPx()
                    val tailX = size.width * 0.75f
                    val tailY = size.height * 0.8f

                    moveTo(tailX, tailY)
                    lineTo(tailX + tailWidth, tailY)
                    lineTo(tailX + tailWidth / 2, tailY + tailHeight) // Center the tip
                    close()
                }
            )
        }
    }
}