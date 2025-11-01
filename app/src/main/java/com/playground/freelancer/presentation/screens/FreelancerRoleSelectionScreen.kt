package com.playground.freelancer.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.freelancer.presentation.components.FreelancerRolePillChip
import com.playground.freelancer.presentation.uistate.FreelancerRoleSelectionUiState
import com.playground.freelancer.presentation.theme.FreelancerAppTheme
import com.playground.freelancer.data.source.mockFreelancerUiState

@Composable
fun FreelancerRoleSelectionScreen(uiState: FreelancerRoleSelectionUiState) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* onBack */ }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                TextButton(onClick = { /* onSkip */ }) {
                    Text("Skip", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        bottomBar = {
            Button(
                onClick = { /* onContinue */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Continue", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Select the role\nthat suits your needs best",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            TextButton(
                onClick = { /* onSelectAll */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Select all")
            }

            Spacer(Modifier.height(8.dp))

            // Role Selection Grid/FlowRow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
//                mainAxisSpacing = 8.dp,
//                crossAxisSpacing = 8.dp
            ) {
                uiState.roles.forEach { role ->
                    FreelancerRolePillChip(
                        role = role,
                        isSelected = role.id in uiState.selectedRoleIds,
                        onClick = { /* Toggle role */ }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FreelancerRoleSelectionScreenPreviewWrapper() {
    FreelancerAppTheme {
        FreelancerRoleSelectionScreen(mockFreelancerUiState)
    }
}
