package com.playground.loose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.loose.mediaplayer.Screen
import com.loose.mediaplayer.ui.viewmodel.PlayerViewModel
import java.util.Locale

private val MINI_PLAYER_HEIGHT = 72.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioLibraryTopAppBar(
    searchFocusRequester: FocusRequester,
    searchQuery: String,
    isSearchActive: Boolean,
    viewMode: ViewMode,
    sortOption: SortOption,
    onViewModeChange: (ViewMode) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: (Boolean) -> Unit,
) {
    TopAppBar(
        title = {
            if (isSearchActive) {
                LibrarySearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    onBack = { onSearchToggle(false); onSearchQueryChange("") },
                    focusRequester = searchFocusRequester,
                    placeholderText = "Search music..."
                )
            } else {
                Text("Music Library", fontSize = 30.sp)
            }
        },
        actions = {
            if (!isSearchActive) {
                IconButton(onClick = { onSearchToggle(true) }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
                IconButton(onClick = {
                    onViewModeChange(if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST)
                }) {
                    Icon(
                        imageVector = if (viewMode == ViewMode.LIST) Icons.Filled.GridView else Icons.AutoMirrored.Filled.ViewList,
                        contentDescription = "Toggle View"
                    )
                }
                SortMenuButton(
                    currentSort = sortOption,
                    onSortChange = onSortChange
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun AudioLibraryContent(
    audioItems: List<AudioItem>,
    currentAudioId: Long?,
    viewMode: ViewMode,
    sortOption: SortOption,
    searchQuery: String,
    onAudioClick: (AudioItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredAudio = remember(audioItems, sortOption, searchQuery) {
        audioItems
            .filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        (it.artist?.contains(searchQuery, ignoreCase = true) ?: false)
            }
            .sortedByOption(sortOption)
    }

    if (filteredAudio.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (searchQuery.isNotBlank()) Icons.Filled.SearchOff else Icons.Filled.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Text(
                    text = if (searchQuery.isNotBlank()) "No search results found" else "No audio files found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        if (viewMode == ViewMode.LIST) {
            LazyColumn(modifier = modifier.fillMaxSize()) {
                items(filteredAudio, key = { it.id }) { audio ->
                    AudioItemListItem(
                        audio = audio,
                        isPlaying = audio.id == currentAudioId,
                        onClick = onAudioClick
                    )
//                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier.fillMaxSize()
            ) {
                items(filteredAudio, key = { it.id }) { audio ->
                    AudioGridItem(
                        audio = audio,
                        isPlaying = audio.id == currentAudioId,
                        onClick = { onAudioClick(audio) }
                    )
                }
            }
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
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPlaying) 4.dp else 1.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Album Art
            AsyncImage(
                model = audio.albumArtUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            ),
                            startY = 200f
                        )
                    )
            )

            // Playing indicator
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

            // Duration badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = formatDuration(audio.duration),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // Title and Artist
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = audio.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = audio.artist ?: "Unknown Artist",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun AudioItemListItem(
    audio: AudioItem,
    isPlaying: Boolean,
    onClick: (AudioItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(audio) }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = audio.albumArtUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = audio.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = audio.artist ?: "Unknown Artist",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Spacer(Modifier.width(8.dp))

        if (isPlaying) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Playing",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = formatDuration(audio.duration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


/**
 * =================================================================
 * HELPER FUNCTIONS AND COMPONENTS
 * =================================================================
 */

fun formatDuration(ms: Long): String {
    if (ms <= 0) return "00:00"
    val totalSeconds = ms / 1000
    val hasHours = totalSeconds >= 3600
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hasHours) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}

/**
 * =================================================================
 * CORE SCREEN LOGIC (Updated Wrapper/Tabs/Drill-Down and BottomBar)
 * =================================================================
 */

// MiniPlayer composable signature assumed from usage in original file
@Composable
fun MiniPlayer(
    currentAudio: AudioItem?,
    currentVideo: VideoItem? = null,
    isPlaying: Boolean,
    isAudioMode: Boolean,
    onPlayPause: () -> Unit,
    onShowQueue: () -> Unit,
    onNext: () -> Unit,
    onClick: () -> Unit
) {
    // Determine if we have media to show
    val mediaExists =
        (isAudioMode && currentAudio != null) || (!isAudioMode && currentVideo != null)
    if (!mediaExists) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(MINI_PLAYER_HEIGHT)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art / Thumbnail
            val imageUri =
                if (isAudioMode) currentAudio?.albumArtUri else currentVideo?.thumbnailUri
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.small
                    )
                    .clip(MaterialTheme.shapes.small) // Use clip to ensure shape applies
            )
            Spacer(Modifier.width(12.dp))

            // Title and Artist - FIX applied here
            Column(Modifier.weight(1f)) {
                // Determine the title explicitly based on the mode (Fixes "Unresolved reference 'title'")
                val titleText = if (isAudioMode) currentAudio!!.title else currentVideo!!.title
                Text(
                    text = titleText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )
                // The artist is only available and relevant for audio
                if (isAudioMode) {
                    Text(
                        text = currentAudio!!.artist ?: "Unknown Artist",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Play/Pause Button - FIX: Corrected icon to Pause/PlayArrow
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
            // Next Button - FIX: Corrected icon to SkipNext
            IconButton(onClick = onNext) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Next"
                )
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioLibraryScreen( // This is the new entry point
    viewModel: PlayerViewModel,
    onNavigateToPlayer: () -> Unit,
    navController: NavController
) {
    val audioItems by viewModel.audioItems.collectAsState()
    val audioPlaylists by viewModel.audioPlaylists.collectAsState()
    val viewMode by viewModel.audioViewMode.collectAsState()
    val sortOption by viewModel.audioSortOption.collectAsState()
    val currentAudio by viewModel.currentAudioItem.collectAsState()
    val currentVideo by viewModel.currentVideoItem.collectAsState() // Fetch Video for MiniPlayer
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isAudioMode by viewModel.isAudioMode.collectAsState()

    // Screen State
    var selectedTab by remember { mutableStateOf(AudioLibraryTab.LIBRARY) }
    var showCreateSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var activePlaylist by remember { mutableStateOf<AudioPlaylist?>(null) } // NEW for drill-down

    // NEW: Focus Requester for search TextField
    val searchFocusRequester = remember { FocusRequester() }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Check for media in MiniPlayer
    val currentMediaId = if (isAudioMode) currentAudio?.id else currentVideo?.id

    // Determine the content to show (Drill-down OR Tabs)
    when {
        // State 1: Playlist Tracks Drill-down View
        activePlaylist != null -> {
            PlaylistTrackListScreen(
                playlist = activePlaylist!!,
                allAudioItems = audioItems,
                currentAudioId = currentAudio?.id,
                onAudioClick = { audio ->
                    // Play the selected song within the playlist context (custom queue)
                    val playlistItems = audioItems.filter { it.id in activePlaylist!!.audioIds }
                    val startIndex = playlistItems.indexOf(audio)
                    viewModel.playSelectedAudio(playlistItems, startIndex)
                    onNavigateToPlayer()
                },
                onBack = { activePlaylist = null } // Go back to playlist list
            )
        }

        // State 2: Main Tabs (Library/Playlists)
        else -> {
            Scaffold(
                topBar = {
                    Column {
                        if (selectedTab == AudioLibraryTab.LIBRARY) {
                            AudioLibraryTopAppBar(
                                searchFocusRequester = searchFocusRequester, // Pass FocusRequester
                                searchQuery = searchQuery,
                                isSearchActive = isSearchActive,
                                viewMode = viewMode,
                                sortOption = sortOption,
                                onViewModeChange = viewModel::setAudioViewMode,
                                onSortChange = viewModel::setAudioSort,
                                onSearchQueryChange = { searchQuery = it },
                                onSearchToggle = { isActive ->
                                    isSearchActive = isActive
                                    if (!isActive) searchQuery = "" // Clear on deactivate
                                }
                            )
                        } else {
                            TopAppBar(title = { Text("Playlists") })
                        }

                        // Tab Row
                        TabRow(selectedTabIndex = selectedTab.ordinal) {
                            AudioLibraryTab.entries.forEach { tab ->
                                Tab(
                                    selected = selectedTab == tab,
                                    onClick = { selectedTab = tab },
                                    text = { Text(tab.title) }
                                )
                            }
                        }
                    }
                },
                bottomBar = { // Re-integrated MiniPlayer and NavigationBar
                    Column {
                        // MiniPlayer is displayed only if a media item is present
                        MiniPlayer(
                            currentAudio = currentAudio,
                            currentVideo = currentVideo,
                            isPlaying = isPlaying,
                            isAudioMode = isAudioMode,
                            onPlayPause = viewModel::playPause,
                            onShowQueue = { /* No-op for now */ },
                            onNext = viewModel::playNext,
                            onClick = onNavigateToPlayer
                        )

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
                },
                floatingActionButton = {
                    if (selectedTab == AudioLibraryTab.PLAYLISTS) {
                        FloatingActionButton(onClick = { showCreateSheet = true }) {
                            Icon(Icons.Filled.QueueMusic, "Create New Playlist")
                        }
                    }
                }
            ) { paddingValues ->
                when (selectedTab) {
                    AudioLibraryTab.LIBRARY -> {
                        AudioLibraryContent(
                            audioItems = audioItems,
                            currentAudioId = currentAudio?.id,
                            viewMode = viewMode,
                            sortOption = sortOption,
                            searchQuery = searchQuery,
                            onAudioClick = { audio ->
                                viewModel.playAudio(audio)
                                onNavigateToPlayer()
                            },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }

                    AudioLibraryTab.PLAYLISTS -> {
                        AudioPlaylistContent(
                            playlists = audioPlaylists,
                            onPlaylistClick = { playlist ->
                                activePlaylist = playlist
                            }, // Drill down
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }


    // Bottom Sheet for Playlist Creation (always shown over main content)
    if (showCreateSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreateSheet = false },
            sheetState = sheetState
        ) {
            CreatePlaylistBottomSheet(
                audioItems = audioItems,
                onCreatePlaylist = { name, ids ->
                    viewModel.createNewPlaylist(name, ids)
                    showCreateSheet = false
                },
                onDismiss = { showCreateSheet = false }
            )
        }
    }
}


// 4. AudioPlaylistContent (The "Playlists" Tab - Click navigates to tracks)
@Composable
fun AudioPlaylistContent(
    playlists: List<AudioPlaylist>,
    onPlaylistClick: (AudioPlaylist) -> Unit, // Click to view tracks
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        if (playlists.isEmpty()) {
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Playlists found. Tap '+' to create one.")
                }
            }
        }
        items(playlists) { playlist ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onPlaylistClick(playlist) }) // Drill-down action
                    .padding(vertical = 12.dp, horizontal = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.QueueMusic,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = playlist.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                    contentDescription = "View Tracks"
                )
            }
            HorizontalDivider()
        }
    }
}


// 5. NEW: PlaylistTrackListScreen (Shows the tracks within a playlist)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistTrackListScreen(
    playlist: AudioPlaylist,
    allAudioItems: List<AudioItem>,
    currentAudioId: Long?,
    onAudioClick: (AudioItem) -> Unit,
    onBack: () -> Unit
) {
    val playlistTracks = remember(playlist, allAudioItems) {
        // Filter the full list of audio items to include only those in the playlist
        allAudioItems.filter { it.id in playlist.audioIds }
            // Sort them in the order they were added to the playlist (assumed logic)
            .sortedBy { playlist.audioIds.indexOf(it.id) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playlist.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Playlists"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (playlistTracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("This playlist is empty.")
            }
        } else {
            LazyColumn(contentPadding = paddingValues, modifier = Modifier.fillMaxSize()) {
                items(playlistTracks) { audio ->
                    AudioItemListItem(
                        audio = audio,
                        isPlaying = audio.id == currentAudioId,
                        onClick = onAudioClick
                    )
//                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                }
            }
        }
    }
}