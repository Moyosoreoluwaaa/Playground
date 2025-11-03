package com.loose.mediaplayer

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.loose.mediaplayer.ui.screen.VideoLibraryScreen
import com.loose.mediaplayer.ui.viewmodel.PlayerViewModel
import com.playground.loose.AudioLibraryScreen
import com.playground.loose.AudioPlayerScreen
import com.playground.loose.VideoPlayerScreen

sealed class Screen(val route: String, val title: String) {
    object AudioLibrary : Screen("audio_library", "Audio")
    object VideoLibrary : Screen("video_library", "Video")
    object AudioPlayer : Screen("audio_player", "Now Playing")
    object VideoPlayer : Screen("video_player", "Video Player")
}

@OptIn(UnstableApi::class)
@Composable
fun LooseApp(viewModel: PlayerViewModel = viewModel()) {
    val navController = rememberNavController()

    // Collect playback and library states
    val audioItems by viewModel.audioItems.collectAsState()
    val videoItems by viewModel.videoItems.collectAsState()
    val currentAudio by viewModel.currentAudioItem.collectAsState()
    val currentVideo by viewModel.currentVideoItem.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val audioViewMode by viewModel.audioViewMode.collectAsState()
    val videoViewMode by viewModel.videoViewMode.collectAsState()
    val audioSortOption by viewModel.audioSortOption.collectAsState()
    val videoSortOption by viewModel.videoSortOption.collectAsState()
    val recentlyPlayedVideoIds by viewModel.recentlyPlayedVideoIds.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.AudioLibrary.route
    ) {
        // 🎵 Audio Library
        composable(Screen.AudioLibrary.route) {
            AudioLibraryScreen(
                audioItems = audioItems,
                currentAudioId = currentAudio?.id,
                viewMode = audioViewMode,
                sortOption = audioSortOption,
                onAudioClick = { audio ->
                    viewModel.playAudio(audio, autoPlay = true)
                    navController.navigate(Screen.AudioPlayer.route)
                },
                onViewModeChange = viewModel::setAudioViewMode,
                onSortChange = viewModel::setAudioSort,
                onNavigateToPlayer = {
                    if (currentAudio != null) navController.navigate(Screen.AudioPlayer.route)
                },
                currentAudio = currentAudio,
                isPlaying = isPlaying,
                onPlayPause = viewModel::playPause,
                onNext = viewModel::playNext,
                navController = navController // 👈 Add this
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
                    viewModel.playVideo(video, autoPlay = true)
                    navController.navigate(Screen.VideoPlayer.route)
                },
                onViewModeChange = viewModel::setVideoViewMode,
                onSortChange = viewModel::setVideoSort,
                onNavigateToPlayer = {
                    if (currentVideo != null) navController.navigate(Screen.VideoPlayer.route)
                },
                currentVideo = currentVideo,
                isPlaying = isPlaying,
                onPlayPause = viewModel::playPause,
                onNext = viewModel::playNext,
                recentlyPlayedIds = recentlyPlayedVideoIds,
                navController = navController // 👈 Add this
            )
        }

        // 🎧 Audio Player
        composable(Screen.AudioPlayer.route) {
            AudioPlayerScreen(
                currentAudio = currentAudio,
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration,
                repeatMode = repeatMode,
                onPlayPause = viewModel::playPause,
                onNext = viewModel::playNext,
                onPrevious = viewModel::playPrevious,
                onSeek = viewModel::seekTo,
                onSkipForward = viewModel::skipForward,
                onSkipBackward = viewModel::skipBackward,
                onToggleRepeat = viewModel::toggleRepeatMode,
                onSetPlaybackSpeed = viewModel::setPlaybackSpeed,
                onBack = { navController.popBackStack() }
            )
        }

        // 📺 Video Player
        composable(Screen.VideoPlayer.route) {
            VideoPlayerScreen(
                player = viewModel.player,
                currentVideo = currentVideo,
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration,
                repeatMode = repeatMode,
                onPlayPause = viewModel::playPause,
                onNext = viewModel::playNext,
                onPrevious = viewModel::playPrevious,
                onSeek = viewModel::seekTo,
                onToggleRepeat = viewModel::toggleRepeatMode,
                onSetPlaybackSpeed = viewModel::setPlaybackSpeed,
                onSwitchToAudio = {
                    navController.navigate(Screen.AudioPlayer.route) {
                        popUpTo(Screen.VideoPlayer.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}