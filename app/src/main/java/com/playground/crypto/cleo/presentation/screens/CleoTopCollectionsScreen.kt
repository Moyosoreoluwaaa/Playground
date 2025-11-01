package com.playground.crypto.cleo.presentation.screens

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
import com.playground.crypto.cleo.presentation.components.CleoFilterChip
import com.playground.crypto.cleo.presentation.components.CleoNFTCard
import com.playground.crypto.cleo.presentation.components.CleoTopHeader
import com.playground.crypto.cleo.domain.model.CleoCollectionInterval
import com.playground.crypto.cleo.domain.model.CleoNftCollection
import com.playground.crypto.cleo.presentation.uistate.CleoTopCollectionsEvent
import com.playground.crypto.cleo.presentation.uistate.CleoTopCollectionsUiState
import com.playground.crypto.cleo.presentation.theme.AccentColor
import com.playground.crypto.cleo.presentation.theme.DarkSurface

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


// --- Preview ---

@Preview(showBackground = true)
@Composable
fun CleoTopCollectionsScreenPreview() {
    // Mock UiState
    val mockNft = CleoNftCollection(
        id = "4103",
        title = "Hedge #4103",
        creatorHandle = "dydxofficial",
        // Using a placeholder image URL - replace with actual Coil-loadable URL if testing on device
        imageUrl = "https://picsum.photos/400/600",
        isFavorite = true,
        remainingSeconds = 84754, // 23h 32m 34s
        currentPrice = "0.288 ETH"
    )

    val mockUiState = CleoTopCollectionsUiState(
        recentCollection = mockNft,
        selectedInterval = CleoCollectionInterval.RECENT
    )

    // Using a custom dark theme for the preview
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
        CleoTopCollectionsScreen(
            uiState = mockUiState,
            onEvent = {}, // No-op for preview
            modifier = Modifier.statusBarsPadding()
        )
    }
}
