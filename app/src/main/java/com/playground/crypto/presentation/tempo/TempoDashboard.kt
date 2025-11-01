package com.playground.crypto.presentation.tempo

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import com.playground.ui.theme.PlaygroundTheme

// Domain models
//data class TempoProduct(
//    val id: String,
//    val name: String,
//    val price: String,
//    val imageUrl: String
//)

data class TempoBanner(
    val id: String,
    val imageUrl: String,
    val title: String,
    val description: String
)

data class TempoDashboardUiState(
    val banners: List<TempoBanner>,
    val newInProducts: List<TempoProduct>,
    val discoverProducts: List<TempoProduct>
)

@Composable
fun TempoSectionHeaderClass(
    title: String,
    onSeeAllClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onSeeAllClicked) {
            Text("See all", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TempoBannerPagerClass(
    banners: List<TempoBanner>,
    onBuyNowClicked: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    Box(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(state = pagerState) { page ->
            val banner = banners[page]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(200.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = banner.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = banner.description,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Button(
                            onClick = { onBuyNowClicked(banner.id) },
                            modifier = Modifier.padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Buy Now")
                        }
                    }
                    AsyncImage(
                        model = banner.imageUrl,
                        contentDescription = "Promotional banner",
                        modifier = Modifier.weight(1f).fillMaxSize()
                    )
                }
            }
        }
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            banners.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .fillMaxWidth()
                        .background(if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.Gray)
                )
            }
        }
    }
}

//@Composable
//fun TempoTopBarClass(
//    title: String,
//    leadingIcon: @Composable () -> Unit,
//    trailingIcons: @Composable () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        leadingIcon()
//        Text(
//            text = title,
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold,
//            color = MaterialTheme.colorScheme.onBackground
//        )
//        trailingIcons()
//    }
//}
//
//@Composable
//fun TempoProductCardClass(
//    product: TempoProduct,
//    onClick: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        onClick = { onClick(product.id) },
//        modifier = modifier
//            .width(150.dp)
//            .padding(8.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//        shape = MaterialTheme.shapes.medium
//    ) {
//        Column(
//            modifier = Modifier.padding(8.dp)
//        ) {
//            AsyncImage(
//                model = product.imageUrl,
//                contentDescription = "Image of ${product.name}",
//                modifier = Modifier
//                    .height(150.dp)
//                    .fillMaxWidth()
//            )
//            Text(
//                text = product.name,
//                fontSize = 14.sp,
//                fontWeight = FontWeight.SemiBold,
//                modifier = Modifier.padding(top = 8.dp)
//            )
//            Text(
//                text = product.price,
//                fontSize = 12.sp,
//                color = MaterialTheme.colorScheme.primary,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//        }
//    }
//}

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
