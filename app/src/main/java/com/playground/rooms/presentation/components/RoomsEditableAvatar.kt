package com.playground.rooms.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoomsEditableAvatar(
    avatarUrl: String?,
    onEditClicked: () -> Unit
) {
    val size = 120.dp
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Avatar Placeholder/Image
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUrl != null) {
                // Use AsyncImage for remote image (placeholder/error fallbacks omitted for brevity)
                // AsyncImage(model = avatarUrl, contentDescription = "Profile picture", ...)
                Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(size * 0.6f), tint = Color.Gray)
            } else {
                Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(size * 0.6f), tint = Color.Gray)
            }
        }

        // Camera Icon Button Overlay
        IconButton(
            onClick = onEditClicked,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-8).dp, y = (-8).dp) // Adjust position to be within the circle edge
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp),
            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Change profile picture",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
