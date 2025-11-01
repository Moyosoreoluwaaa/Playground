package com.playground.crypto.cleo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.crypto.cleo.components.AccentColor
import com.playground.crypto.cleo.components.CleoFilterChip
import com.playground.crypto.cleo.components.CleoNFTCard
import com.playground.crypto.cleo.components.CleoNFTCardFooter
import com.playground.crypto.cleo.components.CleoTopHeader
import com.playground.crypto.cleo.components.DarkSurface
import com.playground.crypto.cleo.uistates.CleoCollectionInterval
import com.playground.crypto.cleo.uistates.CleoNftCollection
import com.playground.crypto.cleo.uistates.CleoTopCollectionsEvent
import com.playground.crypto.cleo.uistates.CleoTopCollectionsUiState

@Composable
fun CleoTopCollectionsScreen(
    uiState: CleoTopCollectionsUiState,
    onEvent: (CleoTopCollectionsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background // Inherit from system
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Cleo Top Header
            CleoTopHeader(
                title = "Top Collections",
                subtitle = "The best of NFT in one place",
                onActionClick = { onEvent(CleoTopCollectionsEvent.GridViewClicked) }
            )

            // 2. Cleo Collection Filter Tabs
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(uiState.availableIntervals) { interval ->
                    CleoFilterChip(
                        text = interval.label,
                        isSelected = interval == uiState.selectedInterval,
                        onClick = { onEvent(CleoTopCollectionsEvent.IntervalSelected(interval)) }
                    )
                }
            }

            // 3. Cleo Recent Collection Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Collection",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { onEvent(CleoTopCollectionsEvent.SeeAllClicked) }) {
                    Text(
                        text = "See all",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 4. Cleo NFT Card
            uiState.recentCollection?.let { nft ->
                CleoNFTCard(
                    nft = nft,
                    onFavoriteToggle = { id -> onEvent(CleoTopCollectionsEvent.FavoriteToggleClicked(id)) },
                    onQuickActionClick = { id -> onEvent(CleoTopCollectionsEvent.QuickActionClicked(id)) },
                    onCardClick = { id -> onEvent(CleoTopCollectionsEvent.CollectionCardClicked(id)) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } ?: run {
                // Empty state or loading
                Box(modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp), contentAlignment = Alignment.Center) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = AccentColor)
                    } else {
                        Text("No recent collection available.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "CleoTopCollectionsScreen - Full")
@Composable
fun CleoTopCollectionsScreenPreview() {
    val previewUiState = CleoTopCollectionsUiState(
        isLoading = false,
        availableIntervals = listOf(
            CleoCollectionInterval.RECENT,
            CleoCollectionInterval.TOP,
            CleoCollectionInterval.TRENDING
        ),
        selectedInterval = CleoCollectionInterval.SHOP,
        recentCollection = CleoNftCollection(
            id = "1",
            title = "CryptoPunks",
            creatorHandle = "Larva Labs",
            imageUrl = "https://example.com/cryptopunks.png",
            isFavorite = false,
            remainingSeconds = 56L,
            currentPrice = "1.23 ETH"
        )
    )
    MaterialTheme(colorScheme = darkColorScheme(background = DarkSurface, onSurface = Color.White, onSurfaceVariant = Color.LightGray)) {
        CleoTopCollectionsScreen(
            uiState = previewUiState,
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "CleoTopCollectionsScreen - Loading")
@Composable
fun CleoTopCollectionsScreenLoadingPreview() {
    val previewUiState = CleoTopCollectionsUiState(
        isLoading = true,
        availableIntervals = listOf(CleoCollectionInterval.TRENDING, CleoCollectionInterval.SHOP),
        selectedInterval = CleoCollectionInterval.TRENDING,
        recentCollection = null
    )
    MaterialTheme(colorScheme = darkColorScheme(background = DarkSurface, onSurface = Color.White)) {
        CleoTopCollectionsScreen(
            uiState = previewUiState,
            onEvent = {}
        )
    }
}


// --- Preview ---

@Preview(showBackground = true)
@Composable
fun CleoTopHeaderPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color.Black,
            onSurface = Color.White,
            onSurfaceVariant = Color.LightGray.copy(alpha = 0.7f)
        )
    ) {
        // Applying a dark surface to simulate the screen background
        Surface(color = Color.Black) {
            CleoTopHeader(
                title = "Top Collections",
                subtitle = "The best of NFT in one place",
                onActionClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "CleoFilterChip States")
@Composable
fun CleoFilterChipPreview() {
    val DarkSurface = Color(0xFF121212)
    MaterialTheme(
        colorScheme = darkColorScheme(
            surface = DarkSurface,
            onSurface = Color.White,
        )
    ) {
        // Use a Row to show both states side-by-side
        Row(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Black),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Selected State
            CleoFilterChip(
                text = "Recent",
                isSelected = true,
                onClick = {}
            )
            // Unselected State
            CleoFilterChip(
                text = "Trending",
                isSelected = false,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "CleoNFTCardFooter")
@Composable
fun CleoNFTCardFooterPreview() {
    val AccentColor = Color(0xFF90A4AE)
    MaterialTheme(
        colorScheme = darkColorScheme(
            onSurface = Color.White,
            onSurfaceVariant = Color.LightGray.copy(alpha = 0.7f),
            primary = AccentColor,
            onPrimary = Color.White
        )
    ) {
        // Background to simulate the card's translucent overlay
        Column(modifier = Modifier.background(Color.Black.copy(alpha = 0.8f))) {
            CleoNFTCardFooter(
                remainingTime = "23h : 12m : 34s",
                currentPrice = "0.288 ETH",
                onQuickActionClick = {}
            )
        }
    }
}



