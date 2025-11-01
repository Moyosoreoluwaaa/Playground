package com.playground.freelancer.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.freelancer.data.source.MockFreelancerData
import com.playground.freelancer.data.source.mockFreelancers
import com.playground.freelancer.domain.model.LoadStatus
import com.playground.freelancer.presentation.components.FreelancerPillButton
import com.playground.freelancer.presentation.components.PlayFreelancerCardStack
import com.playground.freelancer.presentation.uistate.FreelancerSearchUiState

@Composable
fun FreelancerSearchResultScreen(
    uiState: FreelancerSearchUiState,
    onBackClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onFreelancerClick: (String) -> Unit = {},
    onFavoriteToggle: (String) -> Unit = {}
)
{
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            // Header Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Search\nfreelancers",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.widthIn(max = 200.dp)
                )
                FreelancerPillButton(
                    icon = Icons.Default.FilterList,
                    text = "Filters",
                    onClick = onFilterClick,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            // Context/Result Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = uiState.searchQuery,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${uiState.totalResults} results",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))

            // Results List
            when (uiState.status) {
                LoadStatus.LOADED -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // We call the stateless version of the stack directly for a more robust preview
                        // that doesn't depend on a ViewModel.
                        PlayFreelancerCardStack(
                            cards = MockFreelancerData.generateProfiles(),
                            onSwipe = { /* No action needed for preview */ }
                        )
                    }
                }

                LoadStatus.LOADING -> {
                    // ... Loading Indicator UX
                }

                else -> {
                    // ... Empty/Error UX
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchResultScreenPreview() {
    MaterialTheme {
         FreelancerSearchResultScreen(
             uiState = FreelancerSearchUiState(
                 searchResults = mockFreelancers
             )
         )
    }
}
