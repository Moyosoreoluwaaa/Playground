package com.playground.healwise.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.playground.healwise.domain.model.DiagramGroup

@Composable
internal fun VennDiagram(
    modifier: Modifier = Modifier,
    groupA: DiagramGroup,
    groupB: DiagramGroup,
    textColor: Color
) {
    // ... (VennDiagram implementation remains the same, using DiagramGroup) ...
    Box(
        modifier = modifier
            .aspectRatio(1.5f)
            .height(100.dp)
    ) { // Added aspect ratio/height for context
        Canvas(modifier = Modifier.fillMaxSize()) {
            val (width, height) = size
            val circleRadius = height / 2.5f
            val overlapDistance = circleRadius / 2f

            val centerA = Offset(width / 2 - overlapDistance, height / 2)
            val centerB = Offset(width / 2 + overlapDistance, height / 2)

            val pathA = Path().apply { addOval(Rect(center = centerA, radius = circleRadius)) }
            val pathB = Path().apply { addOval(Rect(center = centerB, radius = circleRadius)) }

            val finalPathA = Path.combine(PathOperation.Difference, path1 = pathA, path2 = pathB)
            val finalPathB = Path.combine(PathOperation.Difference, path1 = pathB, path2 = pathA)

            drawPath(path = finalPathA, color = groupA.color)
            drawPath(path = finalPathB, color = groupB.color)
        }

        // Overlay the text values
        Text(
            text = groupA.valueFormatted,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxSize(0.5f)
                .padding(top = 10.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            color = textColor
        )
        Text(
            text = groupB.valueFormatted,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxSize(0.5f)
                .padding(top = 10.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            color = textColor
        )
    }
}
