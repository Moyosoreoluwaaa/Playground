package com.playground.oakk.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.oakk.OakkTheme
import com.playground.oakk.components.*
import com.playground.oakk.models.Asset
import com.playground.oakk.models.AssetCategory
import com.playground.oakk.models.InvestmentScore
import com.playground.oakk.models.InvestmentsUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InvestmentsScreen(
    uiState: InvestmentsUiState,
    onAssetClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
//        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- Scoring Card ---
        item {
            ScoringCard(
                diversificationScore = uiState.diversificationScore,
                netWorthScore = uiState.netWorthScore,
                performanceMessage = uiState.performanceMessage
            )
        }

        // --- Assets List ---
        item {
            SectionHeader(title = "TOTAL NET WORTH IN ASSETS")
        }

        uiState.assetCategories.forEach { category ->
            stickyHeader {
                SectionHeader(
                    title = category.title,
                    modifier = Modifier.padding(top = 8.dp) // Add padding for sticky header
                )
            }
            items(category.assets, key = { it.id }) { asset ->
                AssetRow(asset = asset, onClick = onAssetClick)
            }
        }
    }
}

@Composable
private fun ScoringCard(
    diversificationScore: Int,
    netWorthScore: InvestmentScore,
    performanceMessage: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "SCORING",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Diversification Score
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Diversification Score",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                SegmentedProgressBar(progress = diversificationScore, totalSegments = 10)
            }

            // Net Worth Score
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Net Worth Score",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${netWorthScore.value} (+${netWorthScore.change})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                SegmentedProgressBar(
                    progress = netWorthScore.value / 10, // Assuming score is out of 100
                    totalSegments = 10
                )
            }

            InfoChip(
                text = performanceMessage,
                icon = Icons.Default.TrendingUp
            )
        }
    }
}


//@Preview(showBackground = true, backgroundColor = 0xFFF5F5F3)
@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun InvestmentsScreenPreview() {
    val mockAssets = listOf(
        AssetCategory(
            title = "Stocks",
            assets = listOf(
                Asset("1", "AMZN", "Amazon", "(86 shares)", "https://placehold.co/96x96/FF9900/000000?text=A", "SAR 32,000.75", 1.42),
                Asset("2", "SNYY", "Sony", "(43 shares)", "https://placehold.co/96x96/000000/FFFFFF?text=PS", "SAR 3,200.11", 0.81),
                Asset("3", "APPL", "Apple", "(32 shares)", "https://placehold.co/96x96/A3AAAE/FFFFFF?text=A", "SAR 2,000.75", -7.13)
            )
        ),
        AssetCategory(
            title = "Accounts",
            assets = listOf(
                Asset("4", "BOFA", "Bank of America", "", "https://placehold.co/96x96/E31837/FFFFFF?text=B", "SAR 200,000.00", 16.00)
            )
        )
    )
    val mockUiState = InvestmentsUiState(
        isLoading = false,
        diversificationScore = 7,
        netWorthScore = InvestmentScore(value = 54, change = 4),
        performanceMessage = "You got up 4 points this month! Great job",
        assetCategories = mockAssets
    )
    OakkTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            InvestmentsScreen(
                uiState = mockUiState,
                onAssetClick = {}
            )
        }
    }
}
