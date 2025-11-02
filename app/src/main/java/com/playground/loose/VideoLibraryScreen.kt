package com.loose.mediaplayer.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.playground.loose.*

enum class VideoFilter {
    ALL,      // All videos
    SHORTS,   // Portrait videos < 4 minutes
    FULL      // Everything else
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
    recentlyPlayedIds: List<Long> = emptyList()
) {
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(VideoFilter.ALL) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val filteredVideos = remember(videoItems, selectedFilter) {
        when (selectedFilter) {
            VideoFilter.ALL -> videoItems
            VideoFilter.SHORTS -> videoItems.filter {
                it.height > it.width && it.duration < 240000
            }
            VideoFilter.FULL -> videoItems.filter {
                it.height <= it.width || it.duration >= 240000
            }
        }
    }

    val recentVideos = remember(videoItems, recentlyPlayedIds) {
        recentlyPlayedIds.take(10).mapNotNull { id -> videoItems.find { it.id == id } }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            // Wrap top app bar and filters in a Column so they move together
            Column {
                MediumTopAppBar(
                    title = { Text("Video Library") },
                    actions = {
                        IconButton(onClick = {
                            onViewModeChange(
                                if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
                            )
                        }) {
                            Icon(
                                imageVector = if (viewMode == ViewMode.LIST)
                                    Icons.Filled.GridView else Icons.Filled.ViewList,
                                contentDescription = "Toggle view"
                            )
                        }

                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Filled.Sort, contentDescription = "Sort")
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option.name.replace("_", " ")
                                                .lowercase()
                                                .replaceFirstChar { it.uppercase() }
                                        )
                                    },
                                    onClick = {
                                        onSortChange(option)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior
                )

                // 👇 Filters Row is now part of the collapsible area
                VideoFilterTabs(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    shortsCount = videoItems.count {
                        it.height > it.width && it.duration < 240000
                    },
                    fullCount = videoItems.count {
                        it.height <= it.width || it.duration >= 240000
                    }
                )
            }
        },
        bottomBar = {
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
        }
    ) { padding ->
        // Content: collapsible top and lazy list below
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            if (recentVideos.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    ) {
                        Text(
                            text = "Recently Played",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(recentVideos, key = { it.id }) { video ->
                                RecentVideoCard(
                                    video = video,
                                    isPlaying = video.id == currentVideoId,
                                    onClick = { onVideoClick(video) }
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }

            if (filteredVideos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No ${selectedFilter.name.lowercase()} videos found")
                    }
                }
            } else {
                items(filteredVideos, key = { it.id }) { video ->
                    if (viewMode == ViewMode.LIST) {
                        VideoListItem(
                            video = video,
                            isPlaying = video.id == currentVideoId,
                            onClick = { onVideoClick(video) }
                        )
                    } else {
                        VideoGridItem(
                            video = video,
                            isPlaying = video.id == currentVideoId,
                            onClick = { onVideoClick(video) }
                        )
                    }
                }
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
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
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
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
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
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
            .width(160.dp)
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

            // Gradient overlay at bottom
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

            // Duration badge
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

            // Playing indicator
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

            // Title at bottom
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
fun VideoListView(
    videoItems: List<VideoItem>,
    currentVideoId: Long?,
    onVideoClick: (VideoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(videoItems, key = { it.id }) { video ->
            VideoListItem(
                video = video,
                isPlaying = video.id == currentVideoId,
                onClick = { onVideoClick(video) }
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
                    .size(100.dp, 56.dp)
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
                            .size(32.dp)
                    )
                }
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun VideoGridView(
    videoItems: List<VideoItem>,
    currentVideoId: Long?,
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
        items(videoItems, key = { it.id }) { video ->
            VideoGridItem(
                video = video,
                isPlaying = video.id == currentVideoId,
                onClick = { onVideoClick(video) }
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

            // Duration badge
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

            // Playing indicator
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

            // Title overlay
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