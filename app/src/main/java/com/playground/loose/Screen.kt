// Screen.kt - Updated LooseApp with proper MiniPlayer handling

package com.loose.mediaplayer

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.playground.loose.*
import com.loose.mediaplayer.ui.viewmodel.PlayerViewModel
import kotlinx.coroutines.launch

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
    val coroutineScope = rememberCoroutineScope()

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
    val isAudioMode by viewModel.isAudioMode.collectAsState()
    val isVideoAsAudioMode by viewModel.isVideoAsAudioMode.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.AudioLibrary.route
    ) {
        // 🎵 Audio Library
        composable(Screen.AudioLibrary.route) {
            AudioLibraryScreen(
                viewModel = viewModel,
                onNavigateToPlayer = {
                    coroutineScope.launch {
                        MiniPlayerHandler.handleMiniPlayerClick(
                            viewModel = viewModel,
                            currentAudio = currentAudio,
                            currentVideo = currentVideo,
                            isAudioMode = isAudioMode,
                            isVideoAsAudioMode = isVideoAsAudioMode,
                            player = viewModel.player,
                            onNavigateToAudioPlayer = {
                                navController.navigate(Screen.AudioPlayer.route)
                            },
                            onNavigateToVideoPlayer = {
                                navController.navigate(Screen.VideoPlayer.route)
                            }
                        )
                    }
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
                    viewModel.playVideo(video, autoPlay = true)
                    navController.navigate(Screen.VideoPlayer.route)
                },
                onViewModeChange = viewModel::setVideoViewMode,
                onSortChange = viewModel::setVideoSort,
                onNavigateToPlayer = {
                    coroutineScope.launch {
                        MiniPlayerHandler.handleMiniPlayerClick(
                            viewModel = viewModel,
                            currentAudio = currentAudio,
                            currentVideo = currentVideo,
                            isAudioMode = isAudioMode,
                            isVideoAsAudioMode = isVideoAsAudioMode,
                            player = viewModel.player,
                            onNavigateToAudioPlayer = {
                                navController.navigate(Screen.AudioPlayer.route)
                            },
                            onNavigateToVideoPlayer = {
                                navController.navigate(Screen.VideoPlayer.route)
                            }
                        )
                    }
                },
                currentVideo = currentVideo,
                isPlaying = isPlaying,
                onPlayPause = viewModel::playPause,
                onNext = viewModel::playNext,
                recentlyPlayedIds = recentlyPlayedVideoIds,
                navController = navController
            )
        }

        // 🎧 Audio Player
        composable(Screen.AudioPlayer.route) {
            EnhancedAudioPlayerScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToVideoPlayer = {
                    Log.d("Navigation", "🟢 onNavigateToVideoPlayer called from Audio Player")
                    navController.navigate(Screen.VideoPlayer.route) {
                        popUpTo(Screen.AudioPlayer.route) { inclusive = true }
                    }
                }
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
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}