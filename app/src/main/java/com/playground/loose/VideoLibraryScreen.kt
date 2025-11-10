package com.loose.mediaplayer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.playground.loose.LibrarySearchBar
import com.playground.loose.MiniPlayer
import com.playground.loose.Screen
import com.playground.loose.SortOption
import com.playground.loose.VideoFilter
import com.playground.loose.VideoItem
import com.playground.loose.ViewMode
import com.playground.loose.formatDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLibraryScreen(
    videoItems: List<VideoItem>,
    currentVideoId: Long?,
    viewMode: ViewMode,
    sortOption: SortOption,
    onVideoClick: (VideoItem, VideoFilter) -> Unit, // NEW: Pass filter context
    onViewModeChange: (ViewMode) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onNavigateToPlayer: () -> Unit,
    currentVideo: VideoItem? = null,
    isPlaying: Boolean = false,
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    recentlyPlayedIds: List<Long> = emptyList(),
    navController: NavController,
    lastSelectedFilter: VideoFilter = VideoFilter.ALL, // NEW: Restore last tab
    onFilterChange: (VideoFilter) -> Unit = {} // NEW: Save filter changes
) {
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(lastSelectedFilter.ordinal) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val searchFocusRequester = remember { FocusRequester() }

    // Categorize videos
    val allVideos = remember(videoItems) { videoItems }
    val shortsVideos = remember(videoItems) {
        videoItems.filter { it.height > it.width && it.duration < 240000 }
    }
    val fullVideos = remember(videoItems) {
        videoItems.filter { it.height <= it.width || it.duration >= 240000 }
    }

    // Get current tab's videos
    val currentTabVideos = remember(selectedTabIndex, videoItems) {
        when (selectedTabIndex) {
            0 -> allVideos
            1 -> shortsVideos
            2 -> fullVideos
            else -> allVideos
        }
    }

    // Get current filter enum
    val currentFilter = remember(selectedTabIndex) {
        when (selectedTabIndex) {
            0 -> VideoFilter.ALL
            1 -> VideoFilter.SHORTS
            2 -> VideoFilter.FULL
            else -> VideoFilter.ALL
        }
    }

    // Apply search filter (searches across all videos regardless of tab)
    val displayVideos = remember(currentTabVideos, searchQuery) {
        if (searchQuery.isBlank()) {
            currentTabVideos
        } else {
            // Search across ALL videos, not just current tab
            videoItems.filter { it.title.contains(searchQuery, true) }
        }
    }

    // Filter recently played by current tab
    val recentVideos = remember(videoItems, recentlyPlayedIds, currentFilter) {
        val filteredByTab = when (currentFilter) {
            VideoFilter.ALL -> videoItems
            VideoFilter.SHORTS -> videoItems.filter { it.height > it.width && it.duration < 240000 }
            VideoFilter.FULL -> videoItems.filter { it.height <= it.width || it.duration >= 240000 }
        }
        recentlyPlayedIds.take(10)
            .mapNotNull { id -> filteredByTab.find { it.id == id } }
    }

    // Independent scroll states for each tab
    val allScrollState = rememberLazyListState()
    val shortsScrollState = rememberLazyListState()
    val fullScrollState = rememberLazyListState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (isSearchActive) {
                            LibrarySearchBar(
                                searchQuery = searchQuery,
                                onSearchQueryChange = { searchQuery = it },
                                onBack = { isSearchActive = false; searchQuery = "" },
                                focusRequester = searchFocusRequester,
                                placeholderText = "Search videos..."
                            )
                        } else {
                            Text("Video Library", fontSize = 30.sp)
                        }
                    },
                    actions = {
                        if (!isSearchActive) {
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(Icons.Filled.Search, contentDescription = "Search")
                            }
                            IconButton(onClick = {
                                onViewModeChange(
                                    if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
                                )
                            }) {
                                Icon(
                                    if (viewMode == ViewMode.LIST) Icons.Filled.GridView else Icons.AutoMirrored.Filled.ViewList,
                                    contentDescription = "Toggle view"
                                )
                            }
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                            }
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                SortOption.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                option.name.replace("_", " ").lowercase()
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
                    }
                )

                // Tab Row - Only show when not searching
                if (!isSearchActive) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 16.dp
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = {
                                selectedTabIndex = 0
                                onFilterChange(VideoFilter.ALL)
                            },
                            text = { Text("All (${allVideos.size})") },
                            icon = { Icon(Icons.Filled.VideoLibrary, null, Modifier.size(20.dp)) }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = {
                                selectedTabIndex = 1
                                onFilterChange(VideoFilter.SHORTS)
                            },
                            text = { Text("Shorts (${shortsVideos.size})") },
                            icon = { Icon(Icons.Filled.PhoneAndroid, null, Modifier.size(20.dp)) }
                        )
                        Tab(
                            selected = selectedTabIndex == 2,
                            onClick = {
                                selectedTabIndex = 2
                                onFilterChange(VideoFilter.FULL)
                            },
                            text = { Text("Full (${fullVideos.size})") },
                            icon = { Icon(Icons.Filled.Movie, null, Modifier.size(20.dp)) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column {
                if (currentVideoId != null) {
                    MiniPlayer(
                        currentAudio = null,
                        currentVideo = currentVideo,
                        isPlaying = isPlaying,
                        isAudioMode = false,
                        onPlayPause = onPlayPause,
                        onShowQueue = {},
                        onNext = onNext,
                        onClick = onNavigateToPlayer
                    )
                }

                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.MusicNote, null) },
                        label = { Text("Audio") },
                        selected = navController.currentDestination?.route == Screen.AudioLibrary.route,
                        onClick = {
                            navController.navigate(Screen.AudioLibrary.route) {
                                popUpTo(Screen.AudioLibrary.route)
                                launchSingleTop = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.VideoLibrary, null) },
                        label = { Text("Video") },
                        selected = navController.currentDestination?.route == Screen.VideoLibrary.route,
                        onClick = {
                            navController.navigate(Screen.VideoLibrary.route) {
                                popUpTo(Screen.VideoLibrary.route)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        // Content - Use different scroll state for each tab
        val currentScrollState = when (selectedTabIndex) {
            0 -> allScrollState
            1 -> shortsScrollState
            2 -> fullScrollState
            else -> allScrollState
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            state = currentScrollState,
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Recently played section - only if not searching
            if (recentVideos.isNotEmpty() && searchQuery.isBlank()) {
                item {
                    Column(Modifier.fillMaxWidth()) {
                        Text(
                            "Recently Played (${currentFilter.name.lowercase().replaceFirstChar { it.uppercase() }})",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(recentVideos, key = { it.id }) { video ->
                                RecentVideoCard(video, video.id == currentVideoId) {
                                    onVideoClick(video, currentFilter)
                                }
                            }
                        }
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    }
                }
            }

            // Video list/grid
            if (displayVideos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (searchQuery.isEmpty()) "No videos found"
                            else "No results for \"$searchQuery\""
                        )
                    }
                }
            } else {
                items(displayVideos, key = { it.id }) { video ->
                    if (viewMode == ViewMode.LIST)
                        VideoListItem(video, video.id == currentVideoId) {
                            onVideoClick(video, currentFilter)
                        }
                    else
                        VideoGridItem(video, video.id == currentVideoId) {
                            onVideoClick(video, currentFilter)
                        }
                }
            }
        }
    }
}

@Composable
fun RecentVideoCard(
    video: VideoItem,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .aspectRatio(9f / 16f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = video.thumbnailUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 200f
                        )
                    )
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = formatDuration(video.duration),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            if (isPlaying) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Playing",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp)
                )
            }

            Text(
                text = video.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun VideoListItem(
    video: VideoItem,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = video.title,
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
                    text = "${video.width} × ${video.height}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = formatDuration(video.duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(100.dp, 85.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = video.thumbnailUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (isPlaying) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Playing",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(40.dp)
                    )
                }
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun VideoGridItem(
    video: VideoItem,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = video.thumbnailUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = formatDuration(video.duration),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            if (isPlaying) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Playing",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = video.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(6.dp),
                    color = if (isPlaying) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}