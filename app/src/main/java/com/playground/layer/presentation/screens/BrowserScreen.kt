package com.playground.layer.presentation.screens

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.layer.presentation.theme.BrowserBackground
import com.playground.layer.data.source.GENERIC_DAPP_ICON_URL
import com.playground.layer.data.source.NFT_IMAGE_URLS
import com.playground.layer.domain.model.DApp
import com.playground.layer.domain.model.HistoryEntry
import com.playground.layer.domain.model.ScreenState
import com.playground.layer.presentation.components.BrowserSearchField
import com.playground.layer.presentation.components.DAppCard
import com.playground.layer.presentation.components.HistoryItem
import com.playground.layer.presentation.theme.SwapAppTheme
import com.playground.layer.presentation.uistate.BrowserEvent
import com.playground.layer.presentation.uistate.BrowserUiState

@Composable
fun BrowserScreen(state: BrowserUiState, eventSink: (BrowserEvent) -> Unit) {
    // ... (Screen structure remains the same)
    val customColorScheme = MaterialTheme.colorScheme.copy(
        background = BrowserBackground,
        onBackground = MaterialTheme.colorScheme.onBackground
    )

    SwapAppTheme {
        Scaffold(
            containerColor = customColorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                // 1. Header
                item {
                    Text(
                        text = "Browser",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                    )
                }

                // 2. Search Bar
                item {
                    BrowserSearchField(
                        query = state.searchQuery,
                        onQueryChange = { eventSink(BrowserEvent.onQueryChange(it)) },
                        onSearch = { eventSink(BrowserEvent.onNavigate(it)) },
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                // 3. Recommended DApps
                if (state.recommendedDApps.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recommended DApps",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(bottom = 32.dp)
                        ) {
                            items(state.recommendedDApps) { dapp ->
                                DAppCard(
                                    dapp = dapp,
                                    onClick = { eventSink(BrowserEvent.onDAppClicked(it)) })
                            }
                        }
                    }
                }

                // 4. History
                if (state.browsingHistory.isNotEmpty()) {
                    item {
                        Text(
                            text = "History",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(state.browsingHistory) { entry ->
                        HistoryItem(
                            entry = entry,
                            onClick = { eventSink(BrowserEvent.onHistoryItemClicked(it)) })
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}


// --- Preview Function (FIXED) ---

@Preview(showSystemUi = true)
@Composable
fun BrowserScreenPreview() {
    // FIX: Using GENERIC_DAPP_ICON_URL for the icons instead of "url"
    val mockDApps = remember {
        listOf(
            DApp("uni", "Uniswap", "DeFi", GENERIC_DAPP_ICON_URL, "Mapd"),
            DApp("avae", "Avae", "DeFi", GENERIC_DAPP_ICON_URL),
            DApp("opensea", "OpenSea", "Marketplace", GENERIC_DAPP_ICON_URL, "Mapd"),
            DApp("lr", "Lr", "NFT", GENERIC_DAPP_ICON_URL),
        )
    }

    val mockHistory = remember {
        listOf(
            HistoryEntry("uni", "Uniswap", "app.uniswap.org", GENERIC_DAPP_ICON_URL),
            HistoryEntry("opensea", "OpenSea", "opensea.io/explore", GENERIC_DAPP_ICON_URL),
            HistoryEntry("opensan", "OpenSan", "http://opensan.com", GENERIC_DAPP_ICON_URL)
        )
    }

    val previewState = BrowserUiState(
        screenState = ScreenState.READY,
        recommendedDApps = mockDApps,
        browsingHistory = mockHistory
    )

    SwapAppTheme {
        BrowserScreen(state = previewState, eventSink = {})
    }
}