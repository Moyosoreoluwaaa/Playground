package com.playground.player.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.player.domain.model.WaveformMarker
import kotlinx.coroutines.isActive
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    markers: List<WaveformMarker>,
    onMarkerClicked: (markerId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val animationProgress = remember { Animatable(0f) }
    val col =  MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        while (isActive) {
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
            animationProgress.snapTo(0f)
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val centerY = height / 2f

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val tapX = offset.x
                        markers.forEach { marker ->
                            val markerX = marker.position * width
                            if (tapX in (markerX - 20.dp.toPx())..(markerX + 20.dp.toPx())) {
                                onMarkerClicked(marker.id)
                            }
                        }
                    }
                }
        ) {
            // Draw the first wave
            val path1 = Path()

            path1.moveTo(0f, centerY)
            for (i in 0..width.toInt()) {
                val x = i.toFloat()
                val y = centerY + (sin((x / width) * 2 * Math.PI + animationProgress.value * Math.PI * 2) * 50).toFloat()
                path1.lineTo(x, y)
            }
            drawPath(
                path = path1,
                color = col.primary,
                style = Stroke(width = 4.dp.toPx())
            )

            // Draw the second, different wave
            val path2 = Path()
            path2.moveTo(0f, centerY)
            for (i in 0..width.toInt()) {
                val x = i.toFloat()
                val y = centerY + (sin((x / width) * 3 * Math.PI + animationProgress.value * Math.PI * 2) * 35).toFloat()
                path2.lineTo(x, y)
            }
            drawPath(
                path = path2,
                color = col.secondary,
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw markers
            markers.forEach { marker ->
                val markerX = marker.position * width
                drawCircle(
                    color = marker.color,
                    radius = 12.dp.toPx(),
                    center = Offset(markerX, centerY)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = marker.label,
                    style = TextStyle(color = Color.White, fontSize = 12.sp),
                    topLeft = Offset(
                        x = markerX - textMeasurer.measure(marker.label).size.width / 2,
                        y = centerY - textMeasurer.measure(marker.label).size.height / 2
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun WaveformVisualizerPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        WaveformVisualizer(
            markers = listOf(
                WaveformMarker(id = 2, position = 0.3f, label = "2", color = Color.Magenta),
                WaveformMarker(id = 6, position = 0.65f, label = "6", color = Color.Yellow)
            ),
            onMarkerClicked = {}
        )
    }
}
