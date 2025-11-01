package com.playground.crypto.presentation.bookish// In a new file, e.g., components/BookCard.kt

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.playground.R

data class BookCardState(
    val id: String,
    val title: String,
    val coverImageUri: String,
    val isPlaying: Boolean,
    val currentTime: String,
    val progress: Float
)

@Composable
fun BookCard(
    state: BookCardState,
    onPlayPauseClicked: (bookId: String) -> Unit,
    onBookCardClicked: (bookId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(200.dp) // Example fixed width
            .height(260.dp) // Example fixed height
            .clip(RoundedCornerShape(24.dp))
            .clickable { onBookCardClicked(state.id) }
    ) {
        // Image
        AsyncImage(
            model = state.coverImageUri,
            contentDescription = "Cover for book ${state.title}",
            placeholder = painterResource(R.drawable.ic_launcher_background),
            error = painterResource(R.drawable.ic_launcher_background),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
        )

        // Play/Pause button and details
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Play/Pause button
                IconButton(
                    onClick = { onPlayPauseClicked(state.id) },
                    modifier = Modifier
                        .size(48.dp)
                        .semantics {
                            contentDescription = if (state.isPlaying) "Pause" else "Play"
                        }
                ) {
                    Crossfade(targetState = state.isPlaying) { isPlaying ->
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                
                // Current time text
                Text(
                    text = state.currentTime,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}