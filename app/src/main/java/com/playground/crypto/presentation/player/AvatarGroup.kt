package com.playground.crypto.presentation.player

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.playground.R
import kotlinx.coroutines.delay
import java.util.UUID

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.isActive
import kotlin.math.sin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.runtime.Immutable

@Immutable
data class LiveContentUiState(
    val title: String,
    val listenerCount: Int,
    val listenerAvatars: List<String>,
    val commentCount: Int,
    val currentTime: String,
    val contentTag: ContentTag,
    val waveformMarkers: List<WaveformMarker>,
    val isLiked: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@Immutable
enum class ContentTag(val displayName: String) {
    POETRY("Poetry"),
    STORYTELLING("Storytelling"),
    MUSIC("Music"),
    TALK("Talk")
}

@Immutable
data class WaveformMarker(
    val id: Int,
    val position: Float, // 0.0f to 1.0f
    val label: String,
    val color: Color
)

@Composable
fun LiveContentCard(
    title: String,
    listenerCount: Int,
    avatars: List<String>,
    waveformMarkers: List<WaveformMarker>,
    currentTime: String,
    contentTag: String,
    commentCount: Int,
    onLikeClicked: () -> Unit,
    onCommentClicked: () -> Unit,
    onMarkerClicked: (markerId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarGroup(avatars = avatars)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$listenerCount People are listening",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            HeartIconButton(onClick = onLikeClicked)
        }

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Waveform Visualizer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 16.dp)
        ) {
            WaveformVisualizer(
                markers = waveformMarkers,
                onMarkerClicked = onMarkerClicked,
                modifier = Modifier.fillMaxSize()
            )
            // Circular visualizer placeholder
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
                    .align(Alignment.CenterEnd)
            ) {
                // Placeholder for inner icon/visual effect
            }
        }

        // Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(16.dp))
                PillChip(text = contentTag)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCommentClicked) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "View comments",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = commentCount.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
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
// A simple data class to represent a single floating heart animation
data class Heart(val id: String, val startY: Float, val endY: Float, val alpha: Float)

@Composable
private fun HeartIconButton(
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

@Preview
@Composable
fun HeartIconButtonPreview() {
    MaterialTheme {
        Surface {
            HeartIconButton(onClick = {})
        }
    }
}

@Composable
private fun PillChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

@Preview
@Composable
fun PillChipPreview() {
    MaterialTheme {
        Surface {
            PillChip(text = "Poetry")
        }
    }
}

@Composable
fun AvatarGroup(
    avatars: List<String>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        avatars.take(3).forEachIndexed { index, url ->
            Box(
                modifier = Modifier
                    .offset(x = (index * 16).dp)
                    .size(40.dp)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Listener avatar ${index + 1}",
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        if (avatars.size > 3) {
            Box(
                modifier = Modifier
                    .offset(x = (3 * 16).dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+${avatars.size - 3}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun AvatarGroupPreview() {
    MaterialTheme {
        Surface {
            AvatarGroup(
                avatars = listOf(
                    "https://randomuser.me/api/portraits/men/1.jpg",
                    "https://randomuser.me/api/portraits/women/2.jpg",
                    "https://randomuser.me/api/portraits/men/3.jpg",
                    "https://randomuser.me/api/portraits/women/4.jpg",
                    "https://randomuser.me/api/portraits/men/5.jpg"
                )
            )
        }
    }
}

