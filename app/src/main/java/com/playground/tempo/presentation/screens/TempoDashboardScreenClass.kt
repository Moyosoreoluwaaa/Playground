package com.playground.tempo.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.tempo.domain.model.TempoBanner
import com.playground.tempo.domain.model.TempoProduct
import com.playground.tempo.presentation.components.TempoBannerPagerClass
import com.playground.tempo.presentation.components.TempoProductCardClass
import com.playground.tempo.presentation.components.TempoSectionHeaderClass
import com.playground.tempo.presentation.components.TempoTopBarClass
import com.playground.tempo.presentation.uistate.TempoDashboardUiState
import com.playground.ui.theme.PlaygroundTheme

@Composable
fun TempoDashboardScreenClass(
    uiState: TempoDashboardUiState,
    onMenuClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onCartClicked: () -> Unit,
    onBuyNowClicked: (String) -> Unit,
    onProductClicked: (String) -> Unit,
    onSeeAllNewInClicked: () -> Unit,
    onSeeAllDiscoverClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TempoTopBarClass(
                title = "Tempo",
                leadingIcon = {
                    IconButton(onClick = onMenuClicked) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                trailingIcons = {
                    Row {
                        IconButton(onClick = onSearchClicked) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = onCartClicked) {
                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                TempoBannerPagerClass(
                    banners = uiState.banners,
                    onBuyNowClicked = onBuyNowClicked
                )
            }
            item {
                TempoSectionHeaderClass(
                    title = "NEW IN",
                    onSeeAllClicked = onSeeAllNewInClicked
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.newInProducts) { product ->
                        TempoProductCardClass(product = product, onClick = onProductClicked)
                    }
                }
            }
            item {
                TempoSectionHeaderClass(
                    title = "DISCOVER",
                    onSeeAllClicked = onSeeAllDiscoverClicked
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.discoverProducts) { product ->
                        TempoProductCardClass(product = product, onClick = onProductClicked)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TempoDashboardScreenClassPreview() {
    PlaygroundTheme {
        TempoDashboardScreenClass(
            uiState = TempoDashboardUiState(
                banners = listOf(
                    TempoBanner(
                        id = "1",
                        imageUrl = "https://images.unsplash.com/photo-1542272200-c081d6d6e27b?q=80&w=1935&auto=format&fit=crop",
                        title = "Timeless Style.",
                        description = "Discover our latest collection."
                    ),
                    TempoBanner(
                        id = "2",
                        imageUrl = "https://images.unsplash.com/photo-1596707323136-2396e9527f05?q=80&w=1935&auto=format&fit=crop",
                        title = "Seamless Shopping.",
                        description = "Experience the future of fashion."
                    )
                ),
                newInProducts = listOf(
                    TempoProduct(
                        id = "1",
                        name = "Oversized hoodie",
                        price = "$120.82",
                        imageUrl = "https://images.unsplash.com/photo-1620799140401-6b8f3b259d8d?q=80&w=1935&auto=format&fit=crop"
                    ),
                    TempoProduct(
                        id = "2",
                        name = "Sweatshirt",
                        price = "$150.00",
                        imageUrl = "https://images.unsplash.com/photo-1620799140401-6b8f3b259d8d?q=80&w=1935&auto=format&fit=crop"
                    )
                ),
                discoverProducts = listOf(
                    TempoProduct(
                        id = "3",
                        name = "Sneakers",
                        price = "$200.00",
                        imageUrl = "https://images.unsplash.com/photo-1582787864388-349f57270404?q=80&w=1935&auto=format&fit=crop"
                    ),
                    TempoProduct(
                        id = "4",
                        name = "Jacket",
                        price = "$300.00",
                        imageUrl = "https://images.unsplash.com/photo-1549298916-f52d73359d95?q=80&w=1935&auto=format&fit=crop"
                    )
                )
            ),
            onMenuClicked = {},
            onSearchClicked = {},
            onCartClicked = {},
            onBuyNowClicked = {},
            onProductClicked = {},
            onSeeAllNewInClicked = {},
            onSeeAllDiscoverClicked = {}
        )
    }
}
