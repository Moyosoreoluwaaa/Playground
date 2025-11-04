package com.playground.loose// In a new file, e.g., AudioPlaylistComposables.kt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * 5A. AudioPlaylistContent: The view for displaying saved playlists.
 */
@Composable
fun AudioPlaylistContent(
    playlists: List<AudioPlaylist>,
    onPlaylistClick: (AudioPlaylist) -> Unit,
    onNavigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        if (playlists.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Playlists found. Tap '+' to create one.")
                }
            }
        }
        items(playlists) { playlist ->
            PlaylistListItem(
                playlist = playlist,
                onPlaylistClick = {
                    onPlaylistClick(playlist)
                    onNavigateToPlayer()
                }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun PlaylistListItem(playlist: AudioPlaylist, onPlaylistClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPlaylistClick)
            .padding(vertical = 12.dp, horizontal = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.QueueMusic,
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${playlist.audioIds.size} songs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Play"
        )
    }
}

/**
 * 5B. CreatePlaylistBottomSheet: The view for naming and selecting songs.
 */
@Composable
fun CreatePlaylistBottomSheet(
    audioItems: List<AudioItem>,
    onCreatePlaylist: (String, List<Long>) -> Unit,
    onDismiss: () -> Unit
) {
    var playlistName by remember { mutableStateOf("") }
    var selectedItems by remember { mutableStateOf(emptySet<AudioItem>()) }
    val canSave = playlistName.isNotBlank() && selectedItems.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 700.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "Create New Playlist",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Playlist Name Input
        OutlinedTextField(
            value = playlistName,
            onValueChange = { playlistName = it },
            label = { Text("Playlist Name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Select Songs (${selectedItems.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Song Selection List
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(audioItems) { audio ->
                val isSelected = selectedItems.contains(audio)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedItems =
                                if (isSelected) selectedItems - audio else selectedItems + audio
                        }
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.2f
                            ) else Color.Transparent
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { isChecked ->
                            selectedItems =
                                if (isChecked) selectedItems + audio else selectedItems - audio
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(audio.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            audio.artist ?: "Unknown",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                onCreatePlaylist(playlistName, selectedItems.map { it.id }.toList())
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Playlist")
        }
    }
}