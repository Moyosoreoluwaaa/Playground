package com.playground.bookish.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@Composable
fun VerticalParallaxLazyColumn(
    modifier: Modifier = Modifier,
    count: Int,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    itemHeight: Dp = 120.dp,
    listState: LazyListState = rememberLazyListState(),
    itemContent: @Composable (index: Int) -> Unit
) {
    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeight.toPx() }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding
    ) {
        itemsIndexed(items = (0 until count).toList()) { index, _ ->
            val topOffset by remember {
                derivedStateOf {
                    val itemInfo = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                    itemInfo?.offset?.toFloat() ?: 0f
                }
            }

            val itemState by remember(topOffset) {
                derivedStateOf {
                    val scrollProgress = (topOffset.absoluteValue / itemHeightPx).coerceIn(0f, 1f)
                    // Scale down the card as it scrolls up
                    val scale = 1f - (scrollProgress * 0.1f)
                    // Move the card up to create the "stacking" effect
                    val translationY = scrollProgress * itemHeightPx * 0.4f

                    ParallaxItemState(
                        scale = scale,
                        translationY = translationY,
                        // Z-index ensures the correct stacking order
                        zIndex = (count - index).toFloat()
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .graphicsLayer {
                        scaleX = itemState.scale
                        scaleY = itemState.scale
                        translationY = itemState.translationY
                        shadowElevation = 8f * itemState.scale
                    }
            ) {
                itemContent(index)
            }
        }
    }
}

private data class ParallaxItemState(
    val scale: Float,
    val translationY: Float,
    val zIndex: Float
)
