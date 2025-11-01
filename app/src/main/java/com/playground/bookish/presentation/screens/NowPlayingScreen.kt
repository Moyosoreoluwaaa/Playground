package com.playground.bookish.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.playground.bookish.presentation.components.AudioProgressChart
import com.playground.bookish.presentation.components.AudioWaveform
import com.playground.bookish.presentation.components.NowPlayingTopBar
import com.playground.bookish.presentation.components.PlaybackControls
import com.playground.bookish.presentation.uistate.NowPlayingUiState

@Composable
fun NowPlayingScreen(
    uiState: NowPlayingUiState,
    onBackClicked: () -> Unit,
    onOptionsClicked: () -> Unit,
    onLikeToggled: () -> Unit,
    onPlayClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    onSkipForward: () -> Unit,
    onSkipBackward: () -> Unit,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        // Main Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            NowPlayingTopBar(
                title = uiState.bookTitle,
                onBackClicked = onBackClicked,
                onOptionsClicked = onOptionsClicked,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Main Image and Text Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AsyncImage(
                    model = uiState.bookImageUri,
                    contentDescription = "Book cover for ${uiState.bookTitle}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient Overlay at the bottom for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                                startY = 0.6f
                            )
                        )
                )

                // Bottom Content
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = uiState.author,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = uiState.bookTitle,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Listeners & Like Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Listener Profiles
                        uiState.listeners.take(3).forEach { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = "Listener profile image",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${uiState.listeners.size} people are listening",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        // Like Button
                        IconButton(onClick = onLikeToggled) {
                            Icon(
                                imageVector = if (uiState.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like this book",
                                tint = if (uiState.isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Audio Waveform and Chart
            AudioWaveform(
                waveformData = uiState.audioWaveformData,
                progress = uiState.playbackProgress,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Time & Genre Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = uiState.currentPosition, style = MaterialTheme.typography.bodySmall)
                Text(text = uiState.totalTime, style = MaterialTheme.typography.bodySmall)
            }

            AudioProgressChart(
                chartData = uiState.chartData,
                progress = uiState.playbackProgress,
                onProgressChange = onProgressChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Playback Controls
            PlaybackControls(
                isPlaying = uiState.isPlaying,
                onPlayClicked = onPlayClicked,
                onPauseClicked = onPauseClicked,
                onSkipForward = onSkipForward,
                onSkipBackward = onSkipBackward,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

// Final Preview
@Preview(showBackground = true)
@Composable
fun NowPlayingScreenPreview() {
    MaterialTheme {
        val mockUiState = NowPlayingUiState(
            bookTitle = "Rays of the Earth",
            author = "by Lina Kostenko",
            bookImageUri = "https://example.com/now_playing_cover.jpg",
            isLiked = true,
            isPlaying = true,
            listeners = listOf(
                "https://example.com/user1.jpg",
                "https://example.com/user2.jpg",
                "https://example.com/user3.jpg"
            ),
            currentPosition = "03:43",
            totalTime = "40:48",
            audioWaveformData = List(100) { (0.5f + (Math.sin(it.toDouble() * 0.1) * 0.5f)).toFloat() },
            chartData = listOf(0.1f to "Poetry", 0.7f to "212"), // Simplified chart data for preview
            playbackProgress = 0.5f
        )
        NowPlayingScreen(
            uiState = mockUiState,
            onBackClicked = {},
            onOptionsClicked = {},
            onLikeToggled = {},
            onPlayClicked = {},
            onPauseClicked = {},
            onSkipForward = {},
            onSkipBackward = {},
            onProgressChange = {}
        )
    }
}
