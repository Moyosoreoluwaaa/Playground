package com.playground.layer.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.layer.domain.model.AppTab
import com.playground.layer.domain.model.Asset
import com.playground.layer.domain.model.Interval
import com.playground.layer.domain.model.ScreenState
import com.playground.layer.presentation.components.IntervalSelector
import com.playground.layer.presentation.components.MainNavigationBar
import com.playground.layer.presentation.components.PriceChart
import com.playground.layer.presentation.components.ReviewButton
import com.playground.layer.presentation.components.StatCard
import com.playground.layer.presentation.theme.SwapAppTheme
import com.playground.layer.presentation.uistate.AssetDetailEvent
import com.playground.layer.presentation.uistate.AssetDetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailScreen(state: AssetDetailUiState, eventSink: (AssetDetailEvent) -> Unit) {
    SwapAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { eventSink(AssetDetailEvent.onBackClicked) }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    title = { Text(state.asset.name, style = MaterialTheme.typography.titleLarge) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            bottomBar = {
                MainNavigationBar(state.currentSelectedTab) {
                    eventSink(
                        AssetDetailEvent.onTabSelected(
                            it
                        )
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                item {
                    // Price Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                state.currentPrice,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = if (state.isPricePositive) Color.Green else Color.Red,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = state.priceChangePercent,
                                    color = if (state.isPricePositive) Color.Green else Color.Red,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                        // Asset Icon/Ticker (AsyncImage not stubbed)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                state.asset.ticker,
                                style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Chart
                    PriceChart(
                        data = state.chartData,
                        currentPrice = state.currentPrice.substring(1).toFloatOrNull() ?: 0f,
                        isPositiveChange = state.isPricePositive,
                        onIndicatorMove = { eventSink(AssetDetailEvent.onChartIndicatorMove(it)) }
                    )

                    Spacer(Modifier.height(16.dp))

                    // Interval Selector
                    IntervalSelector(
                        state.selectedInterval,
                        { eventSink(AssetDetailEvent.onIntervalChanged(it)) })

                    Spacer(Modifier.height(24.dp))

                    // Statistics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = "Your Balance",
                            value = state.balanceBTC,
                            subValue = state.balanceUSD,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Market Cap",
                            value = state.marketCap,
                            subValue = state.marketCapRank,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = "Volume (24H)",
                            value = state.volume24H,
                            subValue = null,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Circulating Supply",
                            value = state.circulatingSupply,
                            subValue = null,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Action Button Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { eventSink(AssetDetailEvent.onSwapClicked) },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(10)
                        ) { Text("SWAP") }

                        ReviewButton(
                            isEnabled = true,
                            onClick = { eventSink(AssetDetailEvent.onBuyClicked) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(8.dp)) // Space above Bottom Nav
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AssetDetailScreenPreview() {
    val mockChartData =
        remember { List(50) { it.toFloat() * 2 + (Math.random() * 10 - 5).toFloat() } }

    val previewState = AssetDetailUiState(
        screenState = ScreenState.READY,
        asset = Asset(ticker = "BTC", name = "Bitcoin", iconUrl = "https://example.com/btc.png"),
        currentPrice = "$58,320.50",
        priceChangePercent = "+4.5% Today",
        isPricePositive = true,
        chartData = mockChartData,
        selectedInterval = Interval.ONE_DAY,
        balanceBTC = "0.9 BTC",
        balanceUSD = "$52,488.45",
        marketCap = "$1.1 Trillion",
        marketCapRank = "Rank #1",
        volume24H = "$22,388.5",
        circulatingSupply = "19.5 Million",
        currentSelectedTab = AppTab.ACCOUNT
    )

    AssetDetailScreen(state = previewState, eventSink = {})
}