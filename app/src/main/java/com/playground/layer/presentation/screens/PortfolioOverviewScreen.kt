package com.playground.layer.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.* 
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.layer.data.source.MockUiState
import com.playground.layer.domain.model.BottomNavDestination
import com.playground.layer.domain.model.TabType
import com.playground.layer.presentation.components.CustomBottomNavBar
import com.playground.layer.presentation.components.PortfolioSummaryCard
import com.playground.layer.presentation.components.TokenListCard
import com.playground.layer.presentation.uistate.PortfolioOverviewEvent
import com.playground.layer.presentation.uistate.PortfolioOverviewUiState

@Composable
fun PortfolioOverviewScreen(
    uiState: PortfolioOverviewUiState,
    onEvent: (PortfolioOverviewEvent) -> Unit
) {
    val inactiveTabColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.AccountBalanceWallet,
                    contentDescription = "App Logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Portfolio Overview", style = MaterialTheme.typography.titleLarge)
            }
        },
        bottomBar = {
            CustomBottomNavBar(
                selectedDestination = uiState.selectedBottomDestination,
                onDestinationSelected = { onEvent(PortfolioOverviewEvent.BottomNavSelected(it)) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // Use a scrollable column for the entire screen content *below* the top bar
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            // 1. Balance Summary Card (with Tabs)
            PortfolioSummaryCard(
                uiState = uiState,
                onTabSelected = { onEvent(PortfolioOverviewEvent.TabSelected(it)) },
                inactiveTabColor = inactiveTabColor
            )

            // 2. Token List Card (with Header and List items)
            TokenListCard(
                uiState = uiState,
                onEvent = onEvent
            )
        }
    }
}


// =========================================================================
// 4. PREVIEW
// =========================================================================

@Preview
@Composable
fun PortfolioOverviewScreenPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFFF9800),
            surface = Color(0xFF1C1C1E),
            background = Color(0xFF121212),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF3A3A3C)
        )
    ) {
        val (state,
            setState) = remember { mutableStateOf(MockUiState) }

        PortfolioOverviewScreen(
            uiState = state,
            onEvent = { event ->
                when (event) {
                    is PortfolioOverviewEvent.TabSelected -> setState(state.copy(selectedTab = event.type))
                    is PortfolioOverviewEvent.BottomNavSelected -> setState(
                        state.copy(
                            selectedBottomDestination = event.destination
                        )
                    )

                    else -> {}
                }
            }
        )
    }
}