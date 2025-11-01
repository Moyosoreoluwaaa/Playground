package com.playground.cart

// Import domain models from section 8
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.playground.R

// Minimal Spacing Tokens Definition
object Padding {
    val small: Dp = 8.dp
    val medium: Dp = 16.dp
    val large: Dp = 24.dp
}

// Domain/State Mockup
enum class MediaContentType { IMAGE, VIDEO, STACK, TEXT }
data class SocialSticker(val url: String, val relativeOffsetX: Float, val relativeOffsetY: Float, val sizeFactor: Float)
data class SocialChatUpdate(
    val id: String,
    val mediaUrl: String,
    val contentType: MediaContentType,
    val isUnread: Boolean,
    val relativePositionX: Float,
    val relativePositionY: Float,
    val sizeFactor: Float,
    val zIndex: Float, // Added
    val overlays: List<SocialSticker>
)
data class SocialChatsUiState(val isLoading: Boolean = false, val updates: List<SocialChatUpdate> = emptyList(), val error: String? = null)

// Stand-in for YourSocialAppTheme
@Composable fun YourSocialAppTheme(content: @Composable () -> Unit) { MaterialTheme(content = content) }

// --- Reusable Components ---
@Composable
fun SocialAvatarVideoIndicator(modifier: Modifier = Modifier) {
    // This implements the translucent circle with a play icon inside
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(36.dp) // Defined size for the indicator
            .background(Color.Black.copy(alpha = 0.4f)), // Translucent background
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.PlayArrow,
            contentDescription = "Video content available",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Refactored to handle internal sticker positioning
@Composable
fun SocialChatAvatar(update: SocialChatUpdate, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val baseAvatarSize = 64.dp * update.sizeFactor

    Box(modifier = modifier.size(baseAvatarSize)) {
        // A. Base Avatar (The Circular Image)
        Card(
            onClick = { onClick(update.id) },
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = if (update.isUnread) Color(0xFFC70039) else MaterialTheme.colorScheme.surfaceVariant), // Placeholder Color
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = update.mediaUrl,
                contentDescription = "Latest update from ${update.id}",
                contentScale = ContentScale.Crop, // Essential for circular cropping
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(R.drawable.avatar), // Use a generic drawable
                error = painterResource(R.drawable.avatar) // Fallback to an icon on error
            )
            // B. Video Indicator Overlay (CORRECTED ALIGNMENT)
            if (update.contentType == MediaContentType.VIDEO) {
                // Apply Modifier.align() directly to the Composable call
                SocialAvatarVideoIndicator(Modifier.align(Alignment.CenterHorizontally))
            }
        }

        // B. Sticker Overlays
        update.overlays.forEach { sticker ->
            SocialStickerOverlay(
                sticker = sticker,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        // Position based on half the base size (radius) and relative offset
                        x = (baseAvatarSize / 2 * sticker.relativeOffsetX),
                        y = (baseAvatarSize / 2 * sticker.relativeOffsetY)
                    )
            )
        }
    }
}

@Composable fun SocialStoryStackPreview(update: SocialChatUpdate, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = { onClick(update.id) },
        shape = RoundedCornerShape(Padding.small),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        modifier = modifier.size(width = 100.dp * update.sizeFactor, height = 120.dp * update.sizeFactor)
    ) {
        // Placeholder for multiple overlapping image cards
        Text("Stack", modifier = Modifier.padding(Padding.small))
    }
}

@Composable fun SocialStickerOverlay(sticker: SocialSticker, modifier: Modifier = Modifier) {
    // Placeholder for the sticker image/text bubble
    Box(
        modifier = modifier.size(20.dp * sticker.sizeFactor)
            .background(Color.Red.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
            .padding(2.dp)
    ) {
        if (sticker.url.contains("bubble")) {
            Text("I see", style = MaterialTheme.typography.labelSmall, color = Color.Black)
        }
    }
}

// --- Main Screen Composable (FIXED Z-Index & Density) ---
@Composable
fun SocialChatsScreenLayout(
    uiState: SocialChatsUiState,
    onAvatarClicked: (String) -> Unit,
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Padding.medium, vertical = Padding.large),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Chats", style = MaterialTheme.typography.headlineLarge)
                IconButton(onClick = onSearchClicked) {
                    Icon(Icons.Default.Search, contentDescription = "Search for chats or contacts")
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            CircularProgressIndicator(Modifier.padding(paddingValues).fillMaxSize().wrapContentSize())
            return@Scaffold
        }

        BoxWithConstraints(modifier = modifier.padding(paddingValues).fillMaxSize()) {
            val constraintsWidth = constraints.maxWidth
            val constraintsHeight = constraints.maxHeight

            val density = LocalDensity.current

            // Sort by Z-Index before drawing
            uiState.updates.sortedBy { it.zIndex }.forEach { update ->

                // Convert Px to Dp
                val offsetX: Dp
                val offsetY: Dp
                with(density) {
                    offsetX = (constraintsWidth * update.relativePositionX).toDp()
                    offsetY = (constraintsHeight * update.relativePositionY).toDp()
                }

                val itemModifier = Modifier
                    .zIndex(update.zIndex)
                    .offset(x = offsetX, y = offsetY)

                when (update.contentType) {
                    MediaContentType.STACK -> SocialStoryStackPreview(update, onAvatarClicked, itemModifier)
                    else -> SocialChatAvatar(update, onAvatarClicked, itemModifier)
                }
            }
        }
    }
}


// Mock UiState with Z-Index and adjusted offsets for stickers
private val mockUpdates = listOf(
    // Left Avatar (Cookies & Play button)
    SocialChatUpdate("id_left", "https://picsum.photos/400/400?image=10", MediaContentType.VIDEO, true,
        relativePositionX = 0.05f, relativePositionY = 0.15f, sizeFactor = 1.2f, zIndex = 2f,
        overlays = listOf(
            // Pig sticker: Use a public domain sticker image
            SocialSticker("https://cdn.pixabay.com/photo/2013/07/13/11/48/pig-158756_1280.png", relativeOffsetX = -1.2f, relativeOffsetY = 1.2f, sizeFactor = 2.0f),
            SocialSticker("https://picsum.photos/50/50?image=66", relativeOffsetX = -0.5f, relativeOffsetY = 1.5f, sizeFactor = 0.5f)
        )
    ),
    // Central Stack (Stadium image)
    SocialChatUpdate("id_stack", "https://picsum.photos/400/400?image=20", MediaContentType.STACK, false,
        relativePositionX = 0.35f, relativePositionY = 0.05f, sizeFactor = 1.0f, zIndex = 3f,
        overlays = emptyList()
    ),
    // Bottom Mid Avatar (Lowest Z-Index, possibly behind stack)
    SocialChatUpdate("id_mid", "url_3", MediaContentType.VIDEO, true,
        relativePositionX = 0.3f, relativePositionY = 0.5f, sizeFactor = 0.7f, zIndex = 0f,
        overlays = emptyList()
    ),
    // Right Avatar (Man in yellow cap)
    SocialChatUpdate("id_right", "https://picsum.photos/400/400?image=30", MediaContentType.IMAGE, false,
        relativePositionX = 0.6f, relativePositionY = 0.25f, sizeFactor = 1.1f, zIndex = 1f,
        overlays = listOf(
            // "I see" bubble: Use a special URL flag to trigger the text bubble implementation
            SocialSticker("bubble_flag/text", relativeOffsetX = 1.0f, relativeOffsetY = -0.5f, sizeFactor = 1.5f),
            // Eye sticker: Use another sticker image
            SocialSticker("https://cdn.pixabay.com/photo/2016/06/15/07/31/eye-1457811_1280.png", relativeOffsetX = 1.5f, relativeOffsetY = 1.0f, sizeFactor = 1.2f)
        )
    )
)
private val mockUiState = SocialChatsUiState(isLoading = false, updates = mockUpdates)

@Preview(showBackground = true)
@Composable
fun SocialChatsScreenPreview() {
    YourSocialAppTheme {
        SocialChatsScreenLayout(
            uiState = mockUiState,
            onAvatarClicked = { /* */ },
            onSearchClicked = { /* */ }
        )
    }
}