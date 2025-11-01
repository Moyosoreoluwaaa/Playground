package com.playground.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow

// --- Domain Models (Simplified for Preview) ---
data class CartChatThread(
    val id: String,
    val isGroup: Boolean,
    val avatars: List<String>, // URLs
    val title: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val hasDraftOrAttachment: Boolean = false
)

data class CartStory(
    val id: String,
    val imageUrl: String,
    val avatarUrl: String? = null,
    val overlayText: String? = null
)

// --- UiState (Simplified for Preview) ---
data class CartChatsUiState(
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val stories: List<CartStory> = emptyList(),
    val chatThreads: List<CartChatThread> = emptyList(),
    val isSearching: Boolean = false
)

@Composable
fun CartChatListItem(thread: CartChatThread, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Display Logic (Uses isGroup and avatars)
            if (thread.isGroup) {
                // Group: Show Overlapping Avatars
                Row(modifier = Modifier.size(48.dp), verticalAlignment = Alignment.CenterVertically) {
                    thread.avatars.take(3).forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .offset(x = (-12 * index).dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(1.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                .zIndex(3f - index)
                        )
                    }
                }
            } else {
                // Single Person: Show Initial/Placeholder
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    // Assuming 'avatars.first()' holds an initial or placeholder image URL
                    Text(
                        thread.avatars.firstOrNull() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(thread.title, style = MaterialTheme.typography.titleMedium)
                // Use lastMessage
                Text(thread.lastMessage, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(thread.timestamp, style = MaterialTheme.typography.labelSmall, color = Color.Gray)

                // Use hasDraftOrAttachment
                if (thread.hasDraftOrAttachment) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Attachment or Draft", modifier = Modifier.size(16.dp))
                }

                if (thread.unreadCount > 0) {
                    Box(
                        modifier = Modifier.size(20.dp).clip(CircleShape).background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            thread.unreadCount.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

// NOTE: The CartChatsScreen and Preview Composables will remain structurally similar to the last corrected version.

@Preview(showBackground = true)
@Composable
fun CartChatListItemPreview() {
    MaterialTheme(colorScheme = lightColorScheme(
        primary = Color(0xFF6200EE),
        onPrimary = Color.White,
        surface = Color.White,
        surfaceContainerHigh = Color(0xFFF0F0F0),
        secondaryContainer = Color(0xFFE0E0E0)
    )) {
        Column {
            Text("Single Chat (Unread)", modifier = Modifier.padding(16.dp))
            CartChatListItem(thread = sampleChatThreads[0])

            Text("Group Chat (Unread)", modifier = Modifier.padding(16.dp))
            CartChatListItem(thread = sampleChatThreads[1])

            Text("Single Chat (Attachment)", modifier = Modifier.padding(16.dp))
            CartChatListItem(thread = sampleChatThreads[2])
        }
    }
}