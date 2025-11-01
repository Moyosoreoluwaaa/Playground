package com.playground.rooms.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playground.rooms.domain.model.Room

@Composable
fun RoomCard(
    room: Room,
    onClick: (String) -> Unit
) {
    Card(
        onClick = { onClick(room.id) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Room Avatar/Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .size(48.dp)
                    .clip(CircleShape)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder/Avatar Icon - Using a colored circle for the background effect
                Surface(
                    color = room.roomColor.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape
                ) {}
                Icon(
                    imageVector = Icons.Default.Person, // Generic person icon placeholder
                    contentDescription = "Room avatar for ${room.name}",
                    tint = room.roomColor,
                    modifier = Modifier.size(28.dp)
                )
            }


            Spacer(Modifier.width(16.dp))

            // Text content
            Column {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = room.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
