package com.playground.player.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.UUID

// A simple data class to represent a single floating heart animation
data class Heart(val id: String, val startY: Float, val endY: Float, val alpha: Float)

@Composable
fun HeartIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hearts by remember { mutableStateOf<List<Heart>>(emptyList()) }

    IconButton(
        onClick = {
            val newHeart = Heart(
                id = UUID.randomUUID().toString(),
                startY = 0f,
                endY = -200f,
                alpha = 1f
            )
            hearts = hearts + newHeart
            onClick()
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Like content",
            tint = MaterialTheme.colorScheme.primary
        )
    }

    hearts.forEach { heart ->
        val animatedOffsetY by animateFloatAsState(
            targetValue = heart.endY,
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
            label = "heart_offset_y"
        )
        val animatedAlpha by animateFloatAsState(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            label = "heart_alpha"
        )

        LaunchedEffect(Unit) {
            delay(1000) // Wait for animation to finish
            hearts = hearts.filter { it.id != heart.id }
        }

        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .offset(y = animatedOffsetY.dp)
                .alpha(animatedAlpha)
        )
    }
}
