package com.playground.freelancer.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.playground.freelancer.presentation.components.PlayFreelancerCardStack
import com.playground.freelancer.presentation.viewmodel.PlayFreelancerViewModel


/**
 * The main screen that holds the ViewModel and the card stack. (no change)
 */
@Composable
fun PlayFreelancerCardStackScreen(viewModel: PlayFreelancerViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.cardQueue.isEmpty()) {
                Text("No more profiles to show.")
            } else {
                PlayFreelancerCardStack(
                    cards = uiState.cardQueue,
                    onSwipe = { direction -> viewModel.swipeCard(direction) }
                )
            }
        }
    }
}
