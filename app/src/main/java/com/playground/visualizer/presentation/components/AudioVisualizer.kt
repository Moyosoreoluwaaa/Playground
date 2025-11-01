package com.playground.visualizer.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import com.playground.visualizer.domain.model.AudioData

@Composable
fun AudioVisualizer(
    audioData: AudioData,
    modifier: Modifier = Modifier
) {
    val histogramColor = MaterialTheme.colorScheme.surfaceVariant
    val line1Color = MaterialTheme.colorScheme.tertiary
    val line2Color = MaterialTheme.colorScheme.primary

    BoxWithConstraints(modifier = modifier) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val barWidth = width / audioData.waveform.size
        val halfBarWidth = barWidth / 2
        val strokeWidth = 2.toFloat()

        val linePath1 = remember(audioData.reactiveLines) {
            derivedStateOf {
                Path().apply {
                    if (audioData.reactiveLines.isNotEmpty() && audioData.reactiveLines[0].isNotEmpty()) {
                        moveTo(0f, height * (1 - audioData.reactiveLines[0][0]))
                        for (i in 1 until audioData.reactiveLines[0].size) {
                            lineTo(i * barWidth, height * (1 - audioData.reactiveLines[0][i]))
                        }
                    }
                }
            }
        }

        val linePath2 = remember(audioData.reactiveLines) {
            derivedStateOf {
                Path().apply {
                    if (audioData.reactiveLines.size > 1 && audioData.reactiveLines[1].isNotEmpty()) {
                        moveTo(0f, height * (1 - audioData.reactiveLines[1][0]))
                        for (i in 1 until audioData.reactiveLines[1].size) {
                            lineTo(i * barWidth, height * (1 - audioData.reactiveLines[1][i]))
                        }
                    }
                }
            }
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            audioData.waveform.forEachIndexed { index, value ->
                val barHeight = height * value
                drawRect(
                    brush = SolidColor(histogramColor),
                    topLeft = Offset(index * barWidth, height - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth * 0.8f, barHeight)
                )
            }
            drawPath(
                path = linePath1.value,
                color = line1Color,
                style = Stroke(width = strokeWidth)
            )
            drawPath(
                path = linePath2.value,
                color = line2Color,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
