package com.playground.layer.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.layer.data.source.NFT_IMAGE_URLS
import com.playground.layer.domain.model.NftAppTab
import com.playground.layer.domain.model.NftItem
import com.playground.layer.domain.model.NftScreenState
import com.playground.layer.domain.model.NftTabType
import com.playground.layer.presentation.components.NftMainNavigationBar
import com.playground.layer.presentation.components.NftSegmentedControl
import com.playground.layer.presentation.components.NftTabContent
import com.playground.layer.presentation.theme.SwapAppTheme
import com.playground.layer.presentation.uistate.NftEvent
import com.playground.layer.presentation.uistate.NftUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NftScreen(state: NftUiState, eventSink: (NftEvent) -> Unit) {
    val pagerState =
        rememberPagerState(initialPage = state.pagerStatePage) { NftTabType.entries.size }
    val coroutineScope = rememberCoroutineScope()

    // Sync Pager state with UiState tab selection
    LaunchedEffect(state.selectedTab) {
        pagerState.animateScrollToPage(NftTabType.entries.indexOf(state.selectedTab))
    }

    // Sync UiState tab selection with Pager state
    LaunchedEffect(pagerState.currentPage) {
        eventSink(NftEvent.onPagerSwipe(pagerState.currentPage))
    }

    SwapAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { eventSink(NftEvent.onBackClicked) }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    title = { Text("NFTs", style = MaterialTheme.typography.titleLarge) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            bottomBar = {
                NftMainNavigationBar(state.currentSelectedNftAppTab) {
                    eventSink(
                        NftEvent.onNftAppTabSelected(
                            it
                        )
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Collections",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                NftSegmentedControl(
                    selectedTab = state.selectedTab,
                    onTabSelected = { newTab ->
                        eventSink(NftEvent.onTabSelected(newTab))
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(NftTabType.entries.indexOf(newTab))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    NftTabContent(
                        tabType = NftTabType.entries[it],
                        nftItems = state.collectibles,
                        eventSink = eventSink
                    )
                }
            }
        }
    }
}

// --- Preview Function (From Section 16) ---

@Preview(showSystemUi = true)
@Composable
fun NftScreenPreview() {
    // Generate mock data for the preview
    val mockNfts = remember {
        listOf(
            NftItem("url", "Cosmic Bloom", "Art Gems", "5.5", "ETH"),
            NftItem("url", "Ethernon Hills", "Doodles", "2.5", "ETH"),
            NftItem("url", "Ethereum", "CryptoKitty", "2.5", "ETH"),
            NftItem("url", "Metaverse Explorer", "Pixel Pals", "2.5", "ETH"),
            NftItem("url", "Aura of Time", "Digital Works", "1.1", "ETH"),
            NftItem("url", "Midnight City", "Neon Dreams", "3.0", "ETH"),
        ).mapIndexed { index, item ->
            item.copy(imageUrl = NFT_IMAGE_URLS[index % NFT_IMAGE_URLS.size])
        }
    }

    val previewState = NftUiState(
        NftScreenStateVal = NftScreenState.READY,
        selectedTab = NftTabType.COLLECTIBLES,
        collectibles = mockNfts,
        currentSelectedNftAppTab = NftAppTab.PORTFOLIO
    )

    SwapAppTheme {
        NftScreen(state = previewState, eventSink = {})
    }
}