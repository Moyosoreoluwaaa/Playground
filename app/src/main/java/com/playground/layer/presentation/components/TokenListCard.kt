package com.playground.layer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playground.layer.presentation.uistate.PortfolioOverviewEvent
import com.playground.layer.presentation.uistate.PortfolioOverviewUiState

/**
 * NEW COMPOSABLE: Holds the header and the list of token cards.
 */
@Composable
fun TokenListCard(
    uiState: PortfolioOverviewUiState,
    onEvent: (PortfolioOverviewEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // 1. Header (Moved from PortfolioSummaryCard)
            Text(
                text = uiState.selectedTab.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, bottom = 8.dp)
            )

            // 2. The scrollable token list
            if (uiState.tokenList.isNotEmpty()) {
                // Using a Column/LazyColumn arrangement here since the TokenListCard itself is already a card
                // The LazyColumn needs a fixed height if its parent is a Column,
                // but since the parent is the main Column, we can use fillMaxSize()
                // on the LazyColumn inside this Card for a cleaner structure.
                // NOTE: Using a Box to give LazyColumn a constrained height inside the Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            min = 100.dp,
                            max = 500.dp
                        ) // Max height constraint for scrollable card area
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(uiState.tokenList) { token ->
                            // Note: TokenListItem has internal horizontal padding (16.dp)
                            TokenListItem(
                                token = token,
                                onMoreOptionsClicked = {
                                    onEvent(
                                        PortfolioOverviewEvent.TokenMoreOptionsClicked(
                                            it
                                        )
                                    )
                                },
                                onTokenClicked = { onEvent(PortfolioOverviewEvent.TokenClicked(it)) }
                            )
                        }
                    }
                }
            } else {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp), contentAlignment = Alignment.Center
                ) {
                    Text("No assets found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}