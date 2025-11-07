package com.playground.loose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.loose.mediaplayer.Screen

enum class VideoFilter {
    ALL,
    SHORTS,
    FULL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLibraryScreen(
    videoItems: List<VideoItem>,
    currentVideoId: Long?,
    viewMode: ViewMode,
    sortOption: SortOption,
    onVideoClick: (VideoItem) -> Unit,
    onViewModeChange: (ViewMode) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onNavigateToPlayer: () -> Unit,
    currentVideo: VideoItem? = null,
    isPlaying: Boolean = false,
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    recentlyPlayedIds: List<Long> = emptyList(),
    navController: NavController
) {
    var selectedFilter by remember { mutableStateOf(VideoFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }

    val processedVideos = remember(videoItems, selectedFilter, searchQuery, sortOption) {
        val filtered = when (selectedFilter) {
            VideoFilter.ALL -> videoItems
            VideoFilter.SHORTS -> videoItems.filter { it.height > it.width && it.duration < 240000 }
            VideoFilter.FULL -> videoItems.filter { it.height <= it.width || it.duration >= 240000 }
        }
        val searched = if (searchQuery.isBlank()) filtered else filtered.filter {
            it.title.contains(searchQuery, true)
        }
        searched.sortedByOption(sortOption)
    }

    val recentVideos = remember(videoItems, recentlyPlayedIds) {
        recentlyPlayedIds.take(10).mapNotNull { id -> videoItems.find { it.id == id } }
    }

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
                            SortMenuButton(
                                currentSort = sortOption,
                                onSortChange = onSortChange
                            )
                        }
                    }
                )

                if (!isSearchActive) {
                    VideoFilterTabs(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                        shortsCount = videoItems.count { it.height > it.width && it.duration < 240000 },
                        fullCount = videoItems.count { it.height <= it.width || it.duration >= 240000 }
                    )
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
        if (processedVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (searchQuery.isEmpty()) Icons.Filled.VideoLibrary else Icons.Filled.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Text(
                        text = if (searchQuery.isEmpty()) "No videos found" else "No results for \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            if (viewMode == ViewMode.LIST) {
                VideoListView(
                    processedVideos = processedVideos,
                    recentVideos = recentVideos,
                    currentVideoId = currentVideoId,
                    searchQuery = searchQuery,
                    onVideoClick = onVideoClick,
                    modifier = Modifier.padding(padding)
                )
            } else {
                VideoGridView(
                    processedVideos = processedVideos,
                    recentVideos = recentVideos,
                    currentVideoId = currentVideoId,
                    searchQuery = searchQuery,
                    onVideoClick = onVideoClick,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun VideoFilterTabs(
    selectedFilter: VideoFilter,
    onFilterSelected: (VideoFilter) -> Unit,
    shortsCount: Int,
    fullCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == VideoFilter.ALL,
            onClick = { onFilterSelected(VideoFilter.ALL) },
            label = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.VideoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("All")
                }
            }
        )

        FilterChip(
            selected = selectedFilter == VideoFilter.SHORTS,
            onClick = { onFilterSelected(VideoFilter.SHORTS) },
            label = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhoneAndroid,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Shorts ($shortsCount)")
                }
            }
        )

        FilterChip(
            selected = selectedFilter == VideoFilter.FULL,
            onClick = { onFilterSelected(VideoFilter.FULL) },
            label = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Movie,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Full ($fullCount)")
                }
            }
        )
    }
}

@Composable
private fun VideoListView(
    processedVideos: List<VideoItem>,
    recentVideos: List<VideoItem>,
    currentVideoId: Long?,
    searchQuery: String,
    onVideoClick: (VideoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (recentVideos.isNotEmpty() && searchQuery.isBlank()) {
            item {
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        "Recently Played",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentVideos, key = { it.id }) { video ->
                            RecentVideoCard(video, video.id == currentVideoId) {
                                onVideoClick(video)
                            }
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 12.dp))
                }
            }
        }

        items(processedVideos, key = { it.id }) { video ->
            VideoListItem(video, video.id == currentVideoId) { onVideoClick(video) }
//            HorizontalDivider()
        }
    }
}

@Composable
private fun VideoGridView(
    processedVideos: List<VideoItem>,
    recentVideos: List<VideoItem>,
    currentVideoId: Long?,
    searchQuery: String,
    onVideoClick: (VideoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (recentVideos.isNotEmpty() && searchQuery.isBlank()) {
            item(span = { GridItemSpan(2) }) {
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        "Recently Played",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentVideos, key = { it.id }) { video ->
                            RecentVideoCard(video, video.id == currentVideoId) {
                                onVideoClick(video)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        items(processedVideos, key = { it.id }) { video ->
            VideoGridItem(video, video.id == currentVideoId) { onVideoClick(video) }
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
            .width(120.dp)
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
                            )
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = video.thumbnailUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Playing",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(32.dp)
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = formatDuration(video.duration),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = video.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isPlaying) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${video.width} × ${video.height}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
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
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPlaying) 4.dp else 1.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = video.thumbnailUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
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
                            )
                        )
                    )
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Playing",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = video.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}