package com.playground.cart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp

// Models for the complex, scattered component
//data class CartScatteredStory(
//    val id: String,
//    val imageUrl: String,
//    val shape: Shape,
//    val size: Dp,
//    val offset: Pair<Dp, Dp>, // x, y offset from the starting position
//    val rotation: Float,
//    val zIndex: Float = 0f,
//    val label: String? = null,
//    val icon: ImageVector? = null,
//    val backgroundColor: Color = Color.Transparent
//)

//data class CartChatThread(
//    val id: String,
//    val initials: String,
//    val title: String,
//    val message: String,
//    val timestamp: String,
//    val unreadCount: Int,
//    val profileImageUrls: List<String> = emptyList(),
//    val hasAttachment: Boolean = false
//)
//
//data class CartChatsUiState(
//    // Added the stories list
//    val scatteredStories: List<CartScatteredStory> = emptyScatteredStories,
//    val chatThreads: List<CartChatThread> = sampleChatThreads
//)