package com.playground.rooms.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.shape.CircleShape

// Helper for image placeholder/display
@Composable
fun RoomsProfilePictureArea(
    imageUrl: String?,
    isUploading: Boolean,
    onUploadClicked: () -> Unit,
    onChooseAvatarClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageSize = 140.dp

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Profile Picture Display
        Box(
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape)
                .padding(4.dp), // Placeholder for inner padding/border
            contentAlignment = Alignment.Center
        ) {
            if (isUploading) {
                CircularProgressIndicator(Modifier.size(imageSize * 0.5f))
            } else if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "User profile picture",
                    modifier = Modifier.fillMaxSize(),
                    // Placeholder and error fallback handling is crucial here
                    placeholder = rememberAsyncImagePainter(Icons.Default.AccountCircle),
                    error = rememberAsyncImagePainter(Icons.Default.AccountCircle)
                )
            } else {
                // Default Placeholder
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Default profile picture placeholder",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // 2. Action Links
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onUploadClicked, enabled = !isUploading) {
                Text("Upload a photo")
            }

            Spacer(Modifier.width(16.dp))

            TextButton(onClick = onChooseAvatarClicked, enabled = !isUploading) {
                Text("Choose an avatar")
            }
        }
    }
}
