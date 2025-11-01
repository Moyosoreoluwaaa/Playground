package com.playground.oakk.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.oakk.OakkTheme

/**
 * A progress bar made of discrete segments.
 *
 * @param progress The number of segments to show as filled.
 * @param totalSegments The total number of segments in the bar.
 * @param modifier The modifier to be applied to the component.
 * @param activeColor The color of the filled segments.
 * @param inactiveColor The color of the empty segments.
 */
@Composable
fun SegmentedProgressBar(
    progress: Int,
    totalSegments: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.tertiary,
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(4.dp)),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 1..totalSegments) {
            val color = if (i <= progress) activeColor else inactiveColor
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F3)
@Composable
private fun SegmentedProgressBarPreview() {
    OakkTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SegmentedProgressBar(progress = 7, totalSegments = 10)
            SegmentedProgressBar(progress = 5, totalSegments = 10)
        }
    }
}
