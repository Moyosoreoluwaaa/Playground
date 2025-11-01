package com.playground.cart

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Models for the complex, scattered component (reused for completeness)
data class CartScatteredStory(
    val id: String,
    val imageUrl: String,
    val shape: Shape,
    val size: Dp,
    val offset: Pair<Dp, Dp>, // x, y offset from the starting position
    val rotation: Float,
    val zIndex: Float = 0f,
    val label: String? = null,
    val icon: ImageVector? = null,
    val backgroundColor: Color = Color.Transparent
)

// Placeholder/Mock data for the scattered stories carousel (reused for completeness)
val emptyScatteredStories = listOf(
    CartScatteredStory(
        id = "1", imageUrl = "video_thumb_1", shape = CircleShape, size = 120.dp,
        offset = -40.dp to 0.dp, rotation = -5f, zIndex = 2f, icon = Icons.Default.PlayArrow,
        backgroundColor = Color(0xFFF0E0D0)
    ),
    CartScatteredStory(
        id = "2", imageUrl = "pig_sticker", shape = CircleShape, size = 70.dp,
        offset = -80.dp to 70.dp, rotation = 10f, zIndex = 3f
    ),
    CartScatteredStory(
        id = "3", imageUrl = "center_media", shape = RoundedCornerShape(20.dp), size = 120.dp,
        offset = 80.dp to -40.dp, rotation = 5f, zIndex = 1f
    ),
    CartScatteredStory(
        id = "4", imageUrl = "profile_media", shape = CircleShape, size = 80.dp,
        offset = 210.dp to 50.dp, rotation = -8f, zIndex = 2f
    ),
    CartScatteredStory(
        id = "5", imageUrl = "label", shape = GenericBubbleShape(16.dp), size = 80.dp,
        offset = 220.dp to 120.dp, rotation = 0f, zIndex = 3f, label = "I see",
        backgroundColor = Color(0xFFFFE500)
    ),
    CartScatteredStory(
        id = "6", imageUrl = "far_right", shape = RoundedCornerShape(12.dp), size = 60.dp,
        offset = 350.dp to 20.dp, rotation = 12f, zIndex = 1f
    )
)

// Sample data for the chat list (FIXED: profileImageUrls added)
val sampleAvatars = listOf("url1", "url2", "url3", "url4")

val sampleChatThreads = listOf(
    // 1. Somun Ae-Ri (Single Person)
    CartChatThread(
        id = "c1", isGroup = false, avatars = listOf("S"), title = "Somun Ae-Ri",
        lastMessage = "Don't miss conversations", timestamp = "00:13", unreadCount = 2
    ),
    // 2. Developer team (Group)
    CartChatThread(
        id = "c2", isGroup = true, avatars = sampleAvatars.take(4), title = "Developer team",
        lastMessage = "Slava: Background removal", timestamp = "00:13", unreadCount = 4
    ),
    // 3. Ray Cooper (Single, Has Attachment)
    CartChatThread(
        id = "c3",
        isGroup = false,
        avatars = listOf("R"),
        title = "Ray Cooper",
        lastMessage = "Nicely done!",
        timestamp = "00:13",
        unreadCount = 0,
        hasDraftOrAttachment = true
    ),
    // 4. Developer team (Group)
    CartChatThread(
        id = "c4", isGroup = true, avatars = sampleAvatars.take(3), title = "Developer team",
        lastMessage = "Jain: Nice branding - feels...", timestamp = "00:13", unreadCount = 9
    ),
    // 5. Miriam Jesus (Single)
    CartChatThread(
        id = "c5", isGroup = false, avatars = listOf("M"), title = "Miriam Jesus",
        lastMessage = "Minimal done right", timestamp = "00:13", unreadCount = 0
    ),
    // 6. Mbe... (Single)
    CartChatThread(
        id = "c6", isGroup = false, avatars = listOf("M"), title = "Mbe...",
        lastMessage = "UX po...", timestamp = "00:13", unreadCount = 0
    )
)