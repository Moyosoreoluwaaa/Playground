package com.playground.crypto.cleo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.playground.crypto.cleo.uistates.CleoNftCollection
import com.playground.R


@Composable
fun CleoNFTCard(
    nft: CleoNftCollection,
    onFavoriteToggle: (String) -> Unit,
    onQuickActionClick: (String) -> Unit,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onCardClick(nft.id) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // 1. Image as Background (Bottom Layer) with Placeholder/Error Fallback
            AsyncImage(
                model = nft.imageUrl,
                contentDescription = nft.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF202020)), // Fallback background color if no image

                // --- PLACEHOLDER & ERROR IMPLEMENTATION ---
                error = painterResource(id = R.drawable.grad), // Assuming you have a drawable for error
                placeholder = painterResource(id = R.drawable.input_bg), // Assuming you have a drawable for placeholder
                // Since we don't have access to drawables in this environment,
                // we'll explicitly handle an empty string URL to show a generic icon in the Box:
            )

            // Explicitly handling an empty URL for a cleaner display of the fallback icon
            if (nft.imageUrl.isBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.BrokenImage,
                        contentDescription = "No Image Available",
                        tint = Color.LightGray,
                        modifier = Modifier.size(96.dp)
                    )
                }
            }
            // --- END PLACEHOLDER & ERROR IMPLEMENTATION ---


            // 2. Overlay content (Title and Creator - Top Layer)
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = nft.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White // Force white for visibility on the background image
                )
                Text(
                    text = "by ${nft.creatorHandle}",
                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.LightGray // Force light gray for visibility
                )
            }

            // 3. Favorite Button (Top Layer)
            IconButton(
                onClick = { onFavoriteToggle(nft.id) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.5f)) // Translucent background
            ) {
                Icon(
                    imageVector = if (nft.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Toggle favorite status",
                    tint = if (nft.isFavorite) Color.Red else Color.White
                )
            }

            // 4. Footer (Bottom Layer, positioned on top of the image)
            // Note: CleoNFTCardFooter handles its own centered, translucent background.
            CleoNFTCardFooter(
                remainingTime = formatTime(nft.remainingSeconds),
                currentPrice = nft.currentPrice,
                onQuickActionClick = { onQuickActionClick(nft.id) },
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Position the footer at the bottom
                    .padding(bottom = 16.dp) // Add padding to lift the centered pill up slightly
            )
        }
    }
}

// Simple time formatter stub
fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return "${h}h : ${m}m : ${s}s"
}

@Preview(showBackground = true, name = "CleoNFTCard Featured")
@Composable
fun CleoNFTCardPreview() {
    val DarkSurface = Color(0xFF121212)
    val AccentColor = Color(0xFF90A4AE)

    // Mock data for the card
    val mockNft = CleoNftCollection(
        id = "4103",
        title = "Hedge #4103",
        creatorHandle = "dydxofficial",
        // Placeholder URL for preview image
        imageUrl = "https://picsum.photos/400/600",
        isFavorite = true,
        remainingSeconds = 84754,
        currentPrice = "0.288 ETH"
    )

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color.Black,
            surface = DarkSurface,
            onSurface = Color.White,
            onSurfaceVariant = Color.LightGray.copy(alpha = 0.7f),
            primary = AccentColor,
            onPrimary = Color.White
        )
    ) {
        // Wrap with Surface and padding to simulate the screen context
        Surface(color = Color.Black) {
            Column(modifier = Modifier.padding(16.dp)) {
                CleoNFTCard(
                    nft = mockNft,
                    onFavoriteToggle = {},
                    onQuickActionClick = {},
                    onCardClick = {}
                )
            }
        }
    }
}