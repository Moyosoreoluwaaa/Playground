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
    object VideoAsAudioPlayer : Screen("video_as_audio_player", "Video Audio Mode")
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
    val lastVideoFilter by videoViewModel.lastSelectedFilter.collectAsState()

    // CRITICAL: Context state from SharedViewModel
    val currentContext by sharedViewModel.currentContext.collectAsState()

    // Derived state for backwards compatibility
    val isPlaying = sharedViewModel.isPlayingAnyMedia()

    NavHost(
        navController = navController,
        startDestination = Screen.AudioLibrary.route
    ) {
        // 🎵 Audio Library
        composable(Screen.AudioLibrary.route) {
            AudioLibraryScreen(
                audioViewModel = audioViewModel,
                sharedViewModel = sharedViewModel, // NEW
                currentAudio = currentAudio,
                currentVideo = currentVideo,
                currentContext = currentContext, // NEW: Pass context
                isPlaying = isPlaying,
                navController = navController
            )
        }

        // 🎬 Video Library
        composable(Screen.VideoLibrary.route) {
            VideoLibraryScreen(
                videoItems = videoItems,
                videoViewModel = videoViewModel, // NEW
                sharedViewModel = sharedViewModel, // NEW
                currentVideoId = currentVideo?.id,
                currentContext = currentContext, // NEW: Pass context
                viewMode = videoViewMode,
                sortOption = videoSortOption,
                onViewModeChange = videoViewModel::setVideoViewMode,
                onSortChange = videoViewModel::setVideoSort,
                currentVideo = currentVideo,
                currentAudio = currentAudio, // NEW: For mini player
                isPlaying = isPlaying,
                recentlyPlayedIds = recentlyPlayedVideoIds,
                navController = navController,
                lastSelectedFilter = lastVideoFilter,
                onFilterChange = videoViewModel::setVideoFilter,
                onVideoClick = { video, filterContext ->
                    videoViewModel.playVideo(video, autoPlay = true, filterContext = filterContext)
                    sharedViewModel.switchToVideoMode()
                    navController.navigate(Screen.VideoPlayer.route)
                },
            )
        }

        // 🎧 Audio Player
        composable(Screen.AudioPlayer.route) {
            EnhancedAudioPlayerScreen(
                audioViewModel = audioViewModel,
                videoViewModel = videoViewModel,
                isVideoAsAudioMode = false, // Always false for pure audio
                currentVideo = null, // No video in pure audio mode
                onBack = { navController.popBackStack() },
                onNavigateToVideoPlayer = { } // Not used in pure audio mode
            )
        }

        // 🎵 Video-as-Audio Player
        composable(Screen.VideoAsAudioPlayer.route) {
            VideoAsAudioPlayerScreen(
                videoViewModel = videoViewModel,
                currentVideo = currentVideo,
                onBack = { navController.popBackStack() },
                onNavigateToVideoPlayer = {
                    // FIXED: Switch context BEFORE navigating
                    sharedViewModel.switchToVideoVisualContext()
                    navController.navigate(Screen.VideoPlayer.route) {
                        popUpTo(Screen.VideoAsAudioPlayer.route) { inclusive = true }
                    }
                }
            )
        }

        // 📺 Video Player
        composable(Screen.VideoPlayer.route) {
            VideoPlayerScreen(
                player = videoViewModel.getPlayer(),
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
                    // FIXED: Switch context AND enable video-as-audio mode
                    sharedViewModel.switchToVideoAsAudioContext()
                    videoViewModel.playVideoAsAudio()

                    // Navigate to dedicated screen
                    navController.navigate(Screen.VideoAsAudioPlayer.route) {
                        popUpTo(Screen.VideoPlayer.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}