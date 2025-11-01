package com.loose.mediaplayer.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.playground.loose.AudioItem
import com.playground.loose.SortOption
import com.playground.loose.ViewMode
import com.playground.loose.formatDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioLibraryScreen(
    audioItems: List<AudioItem>,
    currentAudioId: Long?,
    viewMode: ViewMode,
    sortOption: SortOption,
    onAudioClick: (AudioItem) -> Unit,
    onViewModeChange: (ViewMode) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onNavigateToPlayer: () -> Unit,
    currentAudio: AudioItem? = null,
    isPlaying: Boolean = false,
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Music Library") },
                actions = {
                    // View mode toggle
                    IconButton(onClick = {
                        onViewModeChange(
                            if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
                        )
                    }) {
                        Icon(
                            imageVector = if (viewMode == ViewMode.LIST) {
                                Icons.Filled.GridView
                            } else {
                                Icons.Filled.ViewList
                            },
                            contentDescription = "Toggle view"
                        )
                    }

                    // Sort menu
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            imageVector = Icons.Filled.Sort,
                            contentDescription = "Sort"
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Name") },
                            onClick = {
                                onSortChange(SortOption.NAME)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Date Added") },
                            onClick = {
                                onSortChange(SortOption.DATE_ADDED)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Duration") },
                            onClick = {
                                onSortChange(SortOption.DURATION)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Size") },
                            onClick = {
                                onSortChange(SortOption.SIZE)
                                showSortMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentAudioId != null) {
                FloatingActionButton(onClick = onNavigateToPlayer) {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = "Go to player"
                    )
                }
            }
        }
    ) { padding ->
        if (audioItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No audio files found")
            }
        } else {
            when (viewMode) {
                ViewMode.LIST -> AudioListView(
                    audioItems = audioItems,
                    currentAudioId = currentAudioId,
                    onAudioClick = onAudioClick,
                    modifier = Modifier.padding(padding)
                )
                ViewMode.GRID -> AudioGridView(
                    audioItems = audioItems,
                    currentAudioId = currentAudioId,
                    onAudioClick = onAudioClick,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun AudioListView(
    audioItems: List<AudioItem>,
    currentAudioId: Long?,
    onAudioClick: (AudioItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(audioItems, key = { it.id }) { audio ->
            AudioListItem(
                audio = audio,
                isPlaying = audio.id == currentAudioId,
                onClick = { onAudioClick(audio) }
            )
        }
    }
}

@Composable
fun AudioListItem(
    audio: AudioItem,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = audio.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isPlaying) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        },
        supportingContent = {
            Column {
                Text(
                    text = audio.artist ?: "Unknown Artist",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatDuration(audio.duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        leadingContent = {
            AsyncImage(
                model = audio.albumArtUri,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        },
        trailingContent = {
            if (isPlaying) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Playing",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun AudioGridView(
    audioItems: List<AudioItem>,
    currentAudioId: Long?,
    onAudioClick: (AudioItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(audioItems, key = { it.id }) { audio ->
            AudioGridItem(
                audio = audio,
                isPlaying = audio.id == currentAudioId,
                onClick = { onAudioClick(audio) }
            )
        }
    }
}

@Composable
fun AudioGridItem(
    audio: AudioItem,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = audio.albumArtUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = audio.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isPlaying) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = audio.artist ?: "Unknown",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}