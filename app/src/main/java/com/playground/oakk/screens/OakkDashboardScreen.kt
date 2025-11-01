package com.playground.oakk.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.playground.oakk.components.OakkAssetDistributionCard
import com.playground.oakk.components.OakkPillTabRow
import com.playground.oakk.components.OakkSegmentedBarChart
import com.playground.oakk.uistates.OakkDashboardUiState
import com.playground.oakk.uistates.TabType
import com.playground.R
import com.playground.oakk.viewmodels.OakkDashboardViewModel
import com.playground.oakk.OakkTheme
import com.playground.oakk.components.OakkTotalNetWorthCardWithBarChart

@Preview(showBackground = true)
@Composable
fun OakkDashboardScreenPreview() {
    OakkTheme {
        OakkDashboardScreen(
        )
    }
}

// Final OakkDashboardScreen that uses the ViewModel
@Composable
fun OakkDashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: OakkDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Welcome back,\n${uiState.userName}",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                // Placeholder for profile/logo icon
                // Assuming a drawable asset for the logo
                IconButton(
                    onClick = { /* Handle profile click */ },
                    modifier = Modifier.semantics {
                        contentDescription = "User profile and logo"
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.cream), // Replace with your actual drawable
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Tab Row
            OakkPillTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::onTabSelected,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Main Content based on selected tab (conditionally rendered)
            if (uiState.selectedTab == TabType.INVESTMENTS) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OakkTotalNetWorthCardWithBarChart(
                        netWorth = uiState.netWorth,
                        changeText = uiState.netWorthChange,
                        monthlyData = uiState.monthlyData
                    )
                    OakkAssetDistributionCard(categories = uiState.assetCategories)
                }
            }
        }
    }
}
