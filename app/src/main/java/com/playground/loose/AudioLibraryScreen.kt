@file:OptIn(ExperimentalMaterial3Api::class)

package com.playground.loose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.util.Locale

private val MINI_PLAYER_HEIGHT = 72.dp

@androidx.annotation.OptIn(UnstableApi::class)
// Add this at the top of AudioLibraryScreen.kt, replacing the existing function signature:
@Composable
fun AudioLibraryScreen(
    audioViewModel: AudioPlayerViewModel,
    sharedViewModel: SharedMediaViewModel, // NEW: Need shared context
    currentAudio: AudioItem?,
    currentVideo: VideoItem? = null,
    currentContext: PlaybackContext, // NEW: Context instead of isAudioMode
    isPlaying: Boolean,
    navController: NavController
) {
    val audioItems by audioViewModel.audioItems.collectAsState()
    val audioPlaylists by audioViewModel.audioPlaylists.collectAsState()
    val viewMode by audioViewModel.audioViewMode.collectAsState()
    val sortOption by audioViewModel.audioSortOption.collectAsState()

    // Screen State
    var selectedTab by remember { mutableStateOf(AudioLibraryTab.LIBRARY) }
    var showCreateSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var activePlaylist by remember { mutableStateOf<AudioPlaylist?>(null) }

    val searchFocusRequester = remember { FocusRequester() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    when {
        activePlaylist != null -> {
            PlaylistTrackListScreen(
                playlist = activePlaylist!!,
                allAudioItems = audioItems,
                currentAudioId = currentAudio?.id,
                onAudioClick = { audio ->
                    // FIXED: Context switching!
                    sharedViewModel.switchToAudioContext()

                    val playlistItems = audioItems.filter { it.id in activePlaylist!!.audioIds }
                    val startIndex = playlistItems.indexOf(audio)
                    audioViewModel.playSelectedAudio(playlistItems, startIndex)

                    // Navigate to correct screen
                    navController.navigate(Screen.AudioPlayer.route)
                },
                onBack = { activePlaylist = null }
            )
        }

        else -> {
            Scaffold(
                topBar = {
                    Column {
                        if (selectedTab == AudioLibraryTab.LIBRARY) {
                            AudioLibraryTopAppBar(
                                searchFocusRequester = searchFocusRequester,
                                searchQuery = searchQuery,
                                isSearchActive = isSearchActive,
                                viewMode = viewMode,
                                sortOption = sortOption,
                                onViewModeChange = audioViewModel::setAudioViewMode,
                                onSortChange = audioViewModel::setAudioSort,
                                onSearchQueryChange = { searchQuery = it },
                                onSearchToggle = { isActive ->
                                    isSearchActive = isActive
                                    if (!isActive) searchQuery = ""
                                }
                            )
                        } else {
                            TopAppBar(title = { Text("Playlists") })
                        }

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
                bottomBar = {
                    Column {
                        // FIXED: Context-aware MiniPlayer
                        MiniPlayer(
                            currentContext = currentContext,
                            currentAudio = currentAudio,
                            currentVideo = currentVideo,
                            isPlaying = isPlaying,
                            onPlayPause = sharedViewModel::playPause, // Use shared handler
                            onShowQueue = { /* TODO */ },
                            onNext = sharedViewModel::playNext, // Use shared handler
                            onClick = {
                                // FIXED: Navigate based on context
                                when (currentContext) {
                                    PlaybackContext.AUDIO -> navController.navigate(Screen.AudioPlayer.route)
                                    PlaybackContext.VIDEO_VISUAL -> navController.navigate(Screen.VideoPlayer.route)
                                    PlaybackContext.VIDEO_AUDIO_ONLY -> navController.navigate(Screen.VideoAsAudioPlayer.route)
                                    PlaybackContext.NONE -> { /* Do nothing */ }
                                }
                            }
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
                            Icon(Icons.AutoMirrored.Filled.QueueMusic, "Create New Playlist")
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
                                // FIXED: Context switching before playing!
                                sharedViewModel.switchToAudioContext()
                                audioViewModel.playAudio(audio)
                                navController.navigate(Screen.AudioPlayer.route)
                            },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }

                    AudioLibraryTab.PLAYLISTS -> {
                        AudioPlaylistContent(
                            playlists = audioPlaylists,
                            onPlaylistClick = { playlist ->
                                activePlaylist = playlist
                            },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }

    if (showCreateSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreateSheet = false },
            sheetState = sheetState
        ) {
            CreatePlaylistBottomSheet(
                audioItems = audioItems,
                onCreatePlaylist = { name, ids ->
                    audioViewModel.createNewPlaylist(name, ids)
                    showCreateSheet = false
                },
                onDismiss = { showCreateSheet = false }
            )
        }
    }
}

// Keep all other helper functions the same...
// The key change is in the onClick handlers and MiniPlayer usage

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

// 1. AudioLibraryTopAppBar (Updated for Search)
// 1. AudioLibraryTopAppBar (Updated for Search Focus)
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
    var showSortMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (isSearchActive) {
                // Use the new reusable search bar component
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
                // Search Button (sets isSearchActive=true, triggering LaunchedEffect in LibrarySearchBar)
                IconButton(onClick = { onSearchToggle(true) }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
                // View Mode Toggle
                IconButton(onClick = {
                    onViewModeChange(if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST)
                }) {
                    Icon(
                        imageVector = if (viewMode == ViewMode.LIST) Icons.Filled.GridView else Icons.AutoMirrored.Filled.ViewList,
                        contentDescription = "Toggle View"
                    )
                }
                // Sort Menu
                Box {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort Options")
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortOption.entries.forEach { sort ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        sort.name.replace('_', ' ').lowercase()
                                            .replaceFirstChar { it.uppercase() })
                                },
                                onClick = {
                                    onSortChange(sort)
                                    showSortMenu = false
                                },
                                enabled = sort != sortOption
                            )
                        }
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

// 2. AudioItemListItem (Unified list item for Library and Playlist tracks)
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
                .size(100.dp, 85.dp)
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = audio.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = audio.artist ?: "Unknown",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Spacer(Modifier.width(8.dp))

        if (isPlaying) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Playing",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
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

// 3. AudioLibraryContent (The "All Songs" Tab - Updated for Search)
@Composable
fun AudioLibraryContent(
    audioItems: List<AudioItem>,
    currentAudioId: Long?,
    viewMode: ViewMode,
    sortOption: SortOption,
    searchQuery: String, // NEW parameter
    onAudioClick: (AudioItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredAudio = remember(audioItems, sortOption, searchQuery) {
        audioItems
            .filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        (it.artist?.contains(searchQuery, ignoreCase = true) ?: false)
            }
            .sortedWith(compareBy {
                when (sortOption) {
                    SortOption.NAME -> it.title
                    SortOption.DATE_ADDED -> it.dateAdded
                    SortOption.DURATION -> it.duration
                    SortOption.SIZE -> it.size
                }
            })
    }

    if (filteredAudio.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp), contentAlignment = Alignment.Center
        ) {
            Text(if (searchQuery.isNotBlank()) "No search results found" else "No audio files found")
        }
    } else {
        if (viewMode == ViewMode.LIST) {
            LazyColumn(modifier = modifier.fillMaxSize()) {
                items(filteredAudio) { audio ->
                    AudioItemListItem(
                        audio = audio,
                        isPlaying = audio.id == currentAudioId,
                        onClick = onAudioClick
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
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
                // AudioGridItem (placeholder for GRID view)
                items(filteredAudio) { audio ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(180.dp)
                            .clickable { onAudioClick(audio) }) {
                        Text(audio.title, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
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
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                }
            }
        }
    }
}