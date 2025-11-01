package com.playground.layer.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun PercentageChangeLabel(
    percentage: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    val color = when {
        isPositive -> Color(0xFF2ECC71)
        percentage.startsWith("-") -> Color(0xFFE74C3C)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Text(
        text = percentage,
        color = color,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
    )
}