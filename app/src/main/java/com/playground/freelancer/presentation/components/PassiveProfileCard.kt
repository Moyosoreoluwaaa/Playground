package com.playground.freelancer.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.playground.freelancer.presentation.uistate.PlayFreelancerProfileCardUiState

/**
 * The non-interactive cards that form the visual stack.
 * CRITICAL CHANGE: Negative Y-offset applied to make passive cards vertically higher.
 */
@Composable
fun PassiveProfileCard(
    stackIndex: Int,
    uiState: PlayFreelancerProfileCardUiState,
    swipeProgress: Float,
    isNextCard: Boolean
) {
    val density = LocalDensity.current

    // Base properties for stacking
    val scaleFactor = 0.04f
    val yOffsetDp = 30.dp           // Absolute magnitude of the offset

    // Start properties for stack index 1 (next card)
    val baseScale = 1f - (1 * scaleFactor)
    val baseYOffset = -yOffsetDp // <--- CRITICAL: Negative base offset (higher up)

    // Target properties for stack index 0 (top card)
    val targetScale = 1f
    val targetYOffset = 0.dp     // Target offset for top card

    // Color definitions
    val startColor = Color.DarkGray.copy(alpha = 0.5f - (1 * 0.1f))
    val endColor = Color(0xFFDFFF00) // Neon Lime from PlayFreelancerProfileCard

    val currentScale: Float
    val currentYOffset: Dp
    val currentContainerColor: Color

    if (isNextCard) {
        // Linearly interpolate from the negative offset (higher) to 0.dp (lower)
        currentScale = baseScale + (targetScale - baseScale) * swipeProgress
        currentYOffset =
            baseYOffset + (targetYOffset - baseYOffset) * swipeProgress // This interpolates from -16.dp to 0.dp

        // --- GRADUAL COLOR FADE ---
        currentContainerColor = lerp(startColor, endColor, swipeProgress)

    } else {
        // Other passive cards (index 2, 3...) use the standard animation
        // CRITICAL: Apply negative offset for standard passive cards
        val standardYOffset = -(stackIndex * yOffsetDp.value).dp

        val animatedScale by animateFloatAsState(
            targetValue = 1f - (stackIndex * scaleFactor),
            label = "PassiveScale"
        )
        val animatedYOffset by animateDpAsState(
            targetValue = standardYOffset,
            label = "PassiveYOffset"
        )

        currentScale = animatedScale
        currentYOffset = animatedYOffset
        currentContainerColor = Color.DarkGray.copy(alpha = 0.5f - (stackIndex * 0.1f))
    }

    // RENDER LOGIC:
    if (isNextCard && swipeProgress > 0f) {
        // Option 2 (Better): If index 1, use the current container color, and place the content inside.
        Card(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = currentScale
                    scaleY = currentScale
                    translationY = with(density) { currentYOffset.toPx() }
                }
                .zIndex(-stackIndex.toFloat()),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = currentContainerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp * swipeProgress)
        ) {
            PlayFreelancerProfileCardContent(uiState = uiState, isProgressive = true)
        }
    } else {
        // Display the standard passive card (for index 2, 3, or index 1 when swipeProgress is 0)
        Card(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = currentScale
                    scaleY = currentScale
                    translationY = with(density) { currentYOffset.toPx() }
                }
                .zIndex(-stackIndex.toFloat()),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = currentContainerColor)
        ) {}
    }
}
