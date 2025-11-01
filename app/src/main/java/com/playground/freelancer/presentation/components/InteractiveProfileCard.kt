package com.playground.freelancer.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.playground.freelancer.presentation.uistate.PlayFreelancerProfileCardUiState
import com.playground.freelancer.domain.model.PlayFreelancerSwipeDirection
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun InteractiveProfileCard(
    uiState: PlayFreelancerProfileCardUiState,
    onSwipe: (PlayFreelancerSwipeDirection) -> Unit,
    swipeProgress: Animatable<Float, *>
) {
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val density = LocalDensity.current

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    // --- Progressive Transformation Parameters ---
    val scaleFactor = 0.04f
    // yOffsetDp is the absolute magnitude of the translation step (16.dp)
    val yOffsetDp = 16.dp

    val swipeThresholdX = with(density) { screenWidth.toPx() } / 4
    val maxOffsetX = with(density) { screenWidth.toPx() } * 1.5f

    val currentSwipeProgress = remember {
        derivedStateOf {
            (abs(offsetX.value) / maxOffsetX).coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(currentSwipeProgress.value) {
        swipeProgress.snapTo(currentSwipeProgress.value)
    }

    val progressiveScale = 1f - (currentSwipeProgress.value * scaleFactor)
    // When the top card moves, it should still move *down* relative to its starting position,
    // despite the stack below it moving up. Keep this simple positive relative offset.
    val progressiveRelativeYOffset = yOffsetDp * currentSwipeProgress.value


    val pointerInputModifier = Modifier.pointerInput(uiState.id) {
        detectDragGestures(
            onDrag = { change, dragAmount ->
                change.consume()
                scope.launch {
                    offsetX.snapTo(offsetX.value + dragAmount.x)
                    offsetY.snapTo(offsetY.value + dragAmount.y)
                }
            },
            onDragEnd = {
                scope.launch {
                    val targetX: Float
                    val shouldSwipe = abs(offsetX.value) > swipeThresholdX

                    if (shouldSwipe) {
                        targetX =
                            if (offsetX.value > 0) with(density) { screenWidth.toPx() * 1.5f } else with(
                                density
                            ) { -screenWidth.toPx() * 1.5f }
                        launch {
                            offsetX.animateTo(targetX, tween(durationMillis = 300))
                            val direction =
                                if (targetX > 0) PlayFreelancerSwipeDirection.RIGHT else PlayFreelancerSwipeDirection.LEFT
                            onSwipe(direction)
                            offsetX.snapTo(0f)
                            offsetY.snapTo(0f)
                            swipeProgress.snapTo(0f)
                        }
                    } else {
                        targetX = 0f
                        launch {
                            offsetX.animateTo(targetX, tween(durationMillis = 300))
                            swipeProgress.animateTo(0f, tween(durationMillis = 300))
                        }
                    }

                    launch {
                        offsetY.animateTo(0f, tween(durationMillis = 300))
                    }
                }
            }
        )
    }

    PlayFreelancerProfileCard(
        uiState = uiState,
        modifier = pointerInputModifier
            .zIndex(10f)
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .graphicsLayer {
                rotationZ = (offsetX.value / 60).coerceIn(-15f, 15f)
                scaleX = progressiveScale
                scaleY = progressiveScale
                // The interactive card remains anchored at the visual center (0.dp) and moves down relative to it
                translationY = with(density) { (0.dp + progressiveRelativeYOffset).toPx() }
            }
    )
}
