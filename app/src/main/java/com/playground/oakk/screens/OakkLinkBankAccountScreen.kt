package com.playground.oakk.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.oakk.OakkTheme
import com.playground.oakk.components.OakkBankListItem
import com.playground.oakk.components.OakkSearchField
import com.playground.oakk.uistates.OakkBank
import com.playground.oakk.uistates.OakkLinkBankAccountUiState

// region Screen Composable
@Composable
fun OakkLinkBankAccountScreen(
    uiState: OakkLinkBankAccountUiState,
    onBackClicked: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onBankSelected: (OakkBank) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Header Content
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                Text(
                    text = "PERSONAL INFORMATION",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = uiState.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Search Bar
            OakkSearchField(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChanged,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Bank List
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
//                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(uiState.filteredBanks, key = { it.id }) { bank ->
                    OakkBankListItem(
                        bank = OakkBank(
                            id = "1",
                            name = "Chase Bank",
                            logoUrl = "https://example.com/chase.png"
                        ),
                        onClick = {},
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}
// endregion

// region Preview
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun OakkLinkBankAccountScreenPreview() {
    // Assuming OakkTheme is available
    OakkTheme {
        OakkLinkBankAccountScreen(
            uiState = OakkLinkBankAccountUiState(
                searchQuery = "" // Start with an empty search for the main view
            ),
            onBackClicked = { /* no-op */ },
            onSearchQueryChanged = { /* no-op */ },
            onBankSelected = { /* no-op */ }
        )
    }
}

// Example of the ViewModel integration for a real app (not requested, but good context)
/*
class OakkLinkBankAccountViewModel : ViewModel() {
    // ... filtering logic using Flow operators
}
*/