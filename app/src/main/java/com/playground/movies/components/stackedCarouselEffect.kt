//package com.playground.movies.components
//
//import androidx.compose.foundation.lazy.LazyListState
//import androidx.compose.runtime.derivedStateOf
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.composed
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.util.lerp
//import androidx.compose.ui.zIndex
//
///**
// * Custom modifier to create the stacked, scaled card carousel effect using LazyRow scroll state.
// *
// * @param index The index of the item in the list.
// * @param state The current state of the LazyList.
// * @param totalCount The total number of items in the list.
// * @param focusedCardWidth The width of the focused card in DP.
// */
//
//fun Modifier.stackedCarouselEffect(
//    index: Int,
//    state: LazyListState,
//    totalCount: Int,
//    focusedCardWidth: Dp
//): Modifier =
//    composed {
//        val density = LocalDensity.current
//        val focusedCardWidthPx = with(density) { focusedCardWidth.toPx() }
//
//        // Use derivedStateOf to only recompose when the *visible items* change,
//        // NOT on every scroll event. This is necessary for 'offsetFromStart' and 'itemSize'.
//        val itemInfo by remember(state, index) {
//            derivedStateOf {
//                state.layoutInfo.visibleItemsInfo.find { it.index == index }
//            }
//        }
//
//        // These values are stable/infrequently changing within a scroll gesture
//        val offsetFromStart = itemInfo?.offset?.toFloat() ?: 0f
//        val itemSize = itemInfo?.size?.toFloat() ?: focusedCardWidthPx
//        val zIndexValue = (totalCount - index).toFloat()
//
//        // Staggered Offset calculation constants
//        val stackDepth = with(density) { (-40).dp.toPx() }
//        val targetScale = 0.8f
//
//        // 1. Apply ZIndex for drawing order (Stable)
//        // 2. Apply graphicsLayer, moving frequently changing reads inside the lambda.
//        Modifier
//            .zIndex(zIndexValue)
//            .graphicsLayer {
//
//                // Read frequently changing values (scrollOffset, viewportStartOffset)
//                // These reads are now safe inside graphicsLayer's lambda (Draw Phase)
//                val scrollOffset = state.firstVisibleItemScrollOffset.toFloat()
//                val viewportStart = state.layoutInfo.viewportStartOffset
//
//                // Calculation of position relative to the viewport start
//                val startPosition = offsetFromStart - scrollOffset - viewportStart
//
//                // Normalized distance (0.0 to 1.0)
//                val distance = (startPosition / itemSize).coerceIn(0f, 1f)
//
//                // Scale
//                val scale = lerp(targetScale, 1f, 1f - distance)
//
//                // Opacity
//                this.alpha = lerp(0.8f, 1f, 1f - distance)
//
//                // Translation
//                this.translationX = distance * stackDepth
//
//                this.scaleX = scale
//                this.scaleY = scale
//            }
//    }