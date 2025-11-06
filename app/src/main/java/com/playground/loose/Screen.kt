package com.playground.loose

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.loose.mediaplayer.ui.screen.VideoLibraryScreen

sealed class Screen(val route: String, val title: String) {
    object AudioLibrary : Screen("audio_library", "Audio")
    object VideoLibrary : Screen("video_library", "Video")
    object AudioPlayer : Screen("audio_player", "Now Playing")
    object VideoPlayer : Screen("video_player", "Video Player")
}

@OptIn(UnstableApi::class)
@Composable
fun LooseApp(sharedViewModel: SharedMediaViewModel = viewModel()) {
    val navController = rememberNavController()

    // Get child ViewModels
    val audioViewModel = sharedViewModel.audioViewModel
    val videoViewModel = sharedViewModel.videoViewModel

    // Collect states from appropriate ViewModels
    val audioItems by audioViewModel.audioItems.collectAsState()
    val videoItems by videoViewModel.videoItems.collectAsState()
    val currentAudio by audioViewModel.currentAudioItem.collectAsState()
    val currentVideo by videoViewModel.currentVideoItem.collectAsState()

    // Audio states
    val audioIsPlaying by audioViewModel.isPlaying.collectAsState()

    // Video states
    val videoIsPlaying by videoViewModel.isPlaying.collectAsState()
    val videoPosition by videoViewModel.currentPosition.collectAsState()
    val videoDuration by videoViewModel.duration.collectAsState()
    val videoRepeatMode by videoViewModel.repeatMode.collectAsState()
    val videoViewMode by videoViewModel.videoViewMode.collectAsState()
    val videoSortOption by videoViewModel.videoSortOption.collectAsState()
    val recentlyPlayedVideoIds by videoViewModel.recentlyPlayedVideoIds.collectAsState()
    val isVideoAsAudioMode by videoViewModel.isVideoAsAudioMode.collectAsState()

    // Shared state
    val isAudioMode by sharedViewModel.isAudioMode.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.AudioLibrary.route
    ) {
        // 🎵 Audio Library
        composable(Screen.AudioLibrary.route) {
            AudioLibraryScreen(
                audioViewModel = audioViewModel,
                currentAudio = currentAudio,
                currentVideo = currentVideo,
                isAudioMode = isAudioMode,
                isPlaying = audioIsPlaying,
                onNavigateToPlayer = {
                    sharedViewModel.switchToAudioMode()
                    navController.navigate(Screen.AudioPlayer.route)
                },
                navController = navController
            )
        }

        // 🎬 Video Library
        composable(Screen.VideoLibrary.route) {
            VideoLibraryScreen(
                videoItems = videoItems,
                currentVideoId = currentVideo?.id,
                viewMode = videoViewMode,
                sortOption = videoSortOption,
                onVideoClick = { video ->
                    videoViewModel.playVideo(video, autoPlay = true)
                    sharedViewModel.switchToVideoMode()
                    navController.navigate(Screen.VideoPlayer.route)
                },
                onViewModeChange = videoViewModel::setVideoViewMode,
                onSortChange = videoViewModel::setVideoSort,
                onNavigateToPlayer = {
                    if (currentVideo != null) {
                        sharedViewModel.switchToVideoMode()
                        navController.navigate(Screen.VideoPlayer.route)
                    }
                },
                currentVideo = currentVideo,
                isPlaying = videoIsPlaying,
                onPlayPause = videoViewModel::playPause,
                onNext = videoViewModel::playNext,
                recentlyPlayedIds = recentlyPlayedVideoIds,
                navController = navController
            )
        }

        // 🎧 Audio Player
        composable(Screen.AudioPlayer.route) {
            EnhancedAudioPlayerScreen(
                audioViewModel = audioViewModel,
                videoViewModel = videoViewModel,
                isVideoAsAudioMode = isVideoAsAudioMode,
                currentVideo = currentVideo,
                onBack = { navController.popBackStack() },
                onNavigateToVideoPlayer = {
                    sharedViewModel.switchToVideoMode()
                    navController.navigate(Screen.VideoPlayer.route) {
                        popUpTo(Screen.AudioPlayer.route) { inclusive = true }
                    }
                }
            )
        }

        // 📺 Video Player - CRITICAL FIX: Use player from videoViewModel
        composable(Screen.VideoPlayer.route) {
            VideoPlayerScreen(
                player = videoViewModel.getPlayer(), // FIXED: Use the same player instance that videoViewModel controls
                viewModel = videoViewModel,
                currentVideo = currentVideo,
                isPlaying = videoIsPlaying,
                currentPosition = videoPosition,
                duration = videoDuration,
                repeatMode = videoRepeatMode,
                onPlayPause = videoViewModel::playPause,
                onNext = videoViewModel::playNext,
                onPrevious = videoViewModel::playPrevious,
                onSeek = videoViewModel::seekTo,
                onToggleRepeat = videoViewModel::toggleRepeatMode,
                onSetPlaybackSpeed = videoViewModel::setPlaybackSpeed,
                onSwitchToAudio = {
                    sharedViewModel.switchToAudioMode()
                    navController.navigate(Screen.AudioPlayer.route) {
                        popUpTo(Screen.VideoPlayer.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}