package com.playground.loose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.util.Locale

private fun formatDuration(ms: Long): String {
    if (ms <= 0) return "00:00"

    val totalSeconds = ms / 1000

    // Check if the total duration is an hour or more (3600 seconds)
    val hasHours = totalSeconds >= 3600

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hasHours) {
        // Format as H:MM:SS using Locale.US for consistent output
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        // Format as MM:SS using Locale.US for consistent output
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}

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
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

//    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val filteredAudio = remember(audioItems, searchQuery) {
        if (searchQuery.isBlank()) audioItems else
            audioItems.filter {
                it.title.contains(searchQuery, true) ||
                        it.artist?.contains(searchQuery, true) == true ||
                        it.album?.contains(searchQuery, true) == true
            }
    }

    Scaffold(
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search music...") },
                            singleLine = true,
                            leadingIcon = {
                                IconButton(onClick = {
                                    isSearchActive = false
                                    searchQuery = ""
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                }
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Filled.Clear, null)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("Music Library")
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Filled.Search, "Search")
                        }
                        IconButton(onClick = {
                            onViewModeChange(if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST)
                        }) {
                            Icon(
                                if (viewMode == ViewMode.LIST) Icons.Filled.GridView else Icons.AutoMirrored.Filled.ViewList,
                                "Toggle view"
                            )
                        }
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option.name.lowercase()
                                                .replaceFirstChar { it.uppercase() })
                                    },
                                    onClick = {
                                        onSortChange(option)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
//                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            if (currentAudioId != null) {
                MiniPlayer(
                    currentAudio = currentAudio,
                    currentVideo = null,
                    isPlaying = isPlaying,
                    isAudioMode = true,
                    onPlayPause = onPlayPause,
                    onShowQueue = {},
                    onNext = onNext,
                    onClick = onNavigateToPlayer
                )
            }
        }
    ) { padding ->
        if (filteredAudio.isEmpty()) {
            Box(Modifier
                .fillMaxSize()
                .padding(padding), Alignment.Center) {
                Text(if (searchQuery.isEmpty()) "No audio files found" else "No results for \"$searchQuery\"")
            }
        } else {
            when (viewMode) {
                ViewMode.LIST -> AudioListView(
                    filteredAudio,
                    currentAudioId,
                    onAudioClick,
                    Modifier.padding(padding)
                )

                ViewMode.GRID -> AudioGridView(
                    filteredAudio,
                    currentAudioId,
                    onAudioClick,
                    Modifier.padding(padding)
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
                    .size(85.dp)
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

//package com.playground.loose
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AudioLibraryScreen(
//    audioItems: List<AudioItem>,
//    currentAudioId: Long?,
//    viewMode: ViewMode,
//    sortOption: SortOption,
//    onAudioClick: (AudioItem) -> Unit,
//    onViewModeChange: (ViewMode) -> Unit,
//    onSortChange: (SortOption) -> Unit,
//    onNavigateToPlayer: () -> Unit,
//    currentAudio: AudioItem? = null,
//    isPlaying: Boolean = false,
//    onPlayPause: () -> Unit = {},
//    onNext: () -> Unit = {}
//) {
//    var showSortMenu by remember { mutableStateOf(false) }
//    var searchQuery by remember { mutableStateOf("") }
//    var isSearchActive by remember { mutableStateOf(false) }
//
//    // Filter audio items based on search query
//    val filteredAudioItems = remember(audioItems, searchQuery) {
//        if (searchQuery.isBlank()) {
//            audioItems
//        } else {
//            audioItems.filter { audio ->
//                audio.title.contains(searchQuery, ignoreCase = true) ||
//                        audio.artist?.contains(searchQuery, ignoreCase = true) == true ||
//                        audio.album?.contains(searchQuery, ignoreCase = true) == true
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            if (isSearchActive) {
//                // Search Bar
//                SearchBar(
//                    query = searchQuery,
//                    onQueryChange = { searchQuery = it },
//                    onSearch = { },
//                    active = false,
//                    onActiveChange = { },
//                    placeholder = { Text("Search music...") },
//                    leadingIcon = {
//                        IconButton(onClick = {
//                            isSearchActive = false
//                            searchQuery = ""
//                        }) {
//                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
//                        }
//                    },
//                    trailingIcon = {
//                        if (searchQuery.isNotEmpty()) {
//                            IconButton(onClick = { searchQuery = "" }) {
//                                Icon(Icons.Filled.Clear, contentDescription = "Clear")
//                            }
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                ) { }
//            } else {
//                TopAppBar(
//                    title = { Text("Music Library") },
//                    actions = {
//                        // Search button
//                        IconButton(onClick = { isSearchActive = true }) {
//                            Icon(
//                                imageVector = Icons.Filled.Search,
//                                contentDescription = "Search"
//                            )
//                        }
//
//                        // View mode toggle
//                        IconButton(onClick = {
//                            onViewModeChange(
//                                if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
//                            )
//                        }) {
//                            Icon(
//                                imageVector = if (viewMode == ViewMode.LIST) {
//                                    Icons.Filled.GridView
//                                } else {
//                                    Icons.Filled.ViewList
//                                },
//                                contentDescription = "Toggle view"
//                            )
//                        }
//
//                        // Sort menu
//                        IconButton(onClick = { showSortMenu = true }) {
//                            Icon(
//                                imageVector = Icons.Filled.Sort,
//                                contentDescription = "Sort"
//                            )
//                        }
//
//                        DropdownMenu(
//                            expanded = showSortMenu,
//                            onDismissRequest = { showSortMenu = false }
//                        ) {
//                            DropdownMenuItem(
//                                text = { Text("Name") },
//                                onClick = {
//                                    onSortChange(SortOption.NAME)
//                                    showSortMenu = false
//                                }
//                            )
//                            DropdownMenuItem(
//                                text = { Text("Date Added") },
//                                onClick = {
//                                    onSortChange(SortOption.DATE_ADDED)
//                                    showSortMenu = false
//                                }
//                            )
//                            DropdownMenuItem(
//                                text = { Text("Duration") },
//                                onClick = {
//                                    onSortChange(SortOption.DURATION)
//                                    showSortMenu = false
//                                }
//                            )
//                            DropdownMenuItem(
//                                text = { Text("Size") },
//                                onClick = {
//                                    onSortChange(SortOption.SIZE)
//                                    showSortMenu = false
//                                }
//                            )
//                        }
//                    }
//                )
//            }
//        },
//        floatingActionButton = {
//            if (currentAudioId != null) {
//                FloatingActionButton(onClick = onNavigateToPlayer) {
//                    Icon(
//                        imageVector = Icons.Filled.MusicNote,
//                        contentDescription = "Go to player"
//                    )
//                }
//            }
//        }
//    ) { padding ->
//        if (filteredAudioItems.isEmpty()) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    if (searchQuery.isNotEmpty()) {
//                        "No results found for \"$searchQuery\""
//                    } else {
//                        "No audio files found"
//                    }
//                )
//            }
//        } else {
//            when (viewMode) {
//                ViewMode.LIST -> AudioListView(
//                    audioItems = filteredAudioItems,
//                    currentAudioId = currentAudioId,
//                    onAudioClick = onAudioClick,
//                    modifier = Modifier.padding(padding)
//                )
//                ViewMode.GRID -> AudioGridView(
//                    audioItems = filteredAudioItems,
//                    currentAudioId = currentAudioId,
//                    onAudioClick = onAudioClick,
//                    modifier = Modifier.padding(padding)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun AudioListView(
//    audioItems: List<AudioItem>,
//    currentAudioId: Long?,
//    onAudioClick: (AudioItem) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    LazyColumn(
//        modifier = modifier.fillMaxSize(),
//        contentPadding = PaddingValues(8.dp)
//    ) {
//        items(audioItems, key = { it.id }) { audio ->
//            AudioListItem(
//                audio = audio,
//                isPlaying = audio.id == currentAudioId,
//                onClick = { onAudioClick(audio) }
//            )
//        }
//    }
//}
//
//@Composable
//fun AudioListItem(
//    audio: AudioItem,
//    isPlaying: Boolean,
//    onClick: () -> Unit
//) {
//    ListItem(
//        headlineContent = {
//            Text(
//                text = audio.title,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                color = if (isPlaying) {
//                    MaterialTheme.colorScheme.primary
//                } else {
//                    MaterialTheme.colorScheme.onSurface
//                }
//            )
//        },
//        supportingContent = {
//            Column {
//                Text(
//                    text = audio.artist ?: "Unknown Artist",
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    style = MaterialTheme.typography.bodySmall
//                )
//                Text(
//                    text = formatDuration(audio.duration),
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                )
//            }
//        },
//        leadingContent = {
//            AsyncImage(
//                model = audio.albumArtUri,
//                contentDescription = null,
//                modifier = Modifier
//                    .size(56.dp)
//                    .clip(RoundedCornerShape(8.dp)),
//                contentScale = ContentScale.Crop
//            )
//        },
//        trailingContent = {
//            if (isPlaying) {
//                Icon(
//                    imageVector = Icons.Filled.PlayArrow,
//                    contentDescription = "Playing",
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
//        },
//        modifier = Modifier.clickable(onClick = onClick)
//    )
//}
//
//@Composable
//fun AudioGridView(
//    audioItems: List<AudioItem>,
//    currentAudioId: Long?,
//    onAudioClick: (AudioItem) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        modifier = modifier.fillMaxSize(),
//        contentPadding = PaddingValues(8.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        items(audioItems, key = { it.id }) { audio ->
//            AudioGridItem(
//                audio = audio,
//                isPlaying = audio.id == currentAudioId,
//                onClick = { onAudioClick(audio) }
//            )
//        }
//    }
//}
//
//@Composable
//fun AudioGridItem(
//    audio: AudioItem,
//    isPlaying: Boolean,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .aspectRatio(1f)
//            .clickable(onClick = onClick),
//        colors = CardDefaults.cardColors(
//            containerColor = if (isPlaying) {
//                MaterialTheme.colorScheme.primaryContainer
//            } else {
//                MaterialTheme.colorScheme.surface
//            }
//        )
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            AsyncImage(
//                model = audio.albumArtUri,
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f),
//                contentScale = ContentScale.Crop
//            )
//
//            Column(
//                modifier = Modifier.padding(8.dp)
//            ) {
//                Text(
//                    text = audio.title,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = if (isPlaying) {
//                        MaterialTheme.colorScheme.primary
//                    } else {
//                        MaterialTheme.colorScheme.onSurface
//                    }
//                )
//                Text(
//                    text = audio.artist ?: "Unknown",
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
//                )
//            }
//        }
//    }
//}