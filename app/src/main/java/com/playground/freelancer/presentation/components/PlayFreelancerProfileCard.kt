package com.playground.freelancer.presentation.components


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.playground.freelancer.presentation.uistate.PlayFreelancerProfileCardUiState

/**
 * The actual UI content of a single freelancer profile card. (UPDATED to use the Content composable)
 */
@Composable
fun PlayFreelancerProfileCard(
    uiState: PlayFreelancerProfileCardUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDFFF00)), // Neon Lime
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        PlayFreelancerProfileCardContent(uiState = uiState)
    }
}
