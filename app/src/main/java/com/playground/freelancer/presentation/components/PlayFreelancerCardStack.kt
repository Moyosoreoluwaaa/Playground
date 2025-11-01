package com.playground.freelancer.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.freelancer.data.source.MockFreelancerData
import com.playground.freelancer.presentation.uistate.PlayFreelancerProfileCardUiState
import com.playground.freelancer.domain.model.PlayFreelancerSwipeDirection


/**
 * Preview for the entire interactive card stack. (no change)
 * This shows how the cards layer on top of each other.
 */
@Preview(showBackground = true, name = "Full Card Stack")
@Composable
fun PlayFreelancerCardStackScreenPreview() {
    MaterialTheme {
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
}


/**
 * Manages the layout and swipe logic for the entire stack of cards.
 * Adjusted zIndex for the interactive card.
 */

@Composable
fun PlayFreelancerCardStack(
    cards: List<PlayFreelancerProfileCardUiState>,
    onSwipe: (PlayFreelancerSwipeDirection) -> Unit,
    stackDepth: Int = 4
) {
    val swipeProgress = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .width(320.dp)
            .height(300.dp)
            .offset(y = 16.dp), // <--- NEW: Offset the entire stack down 16.dp
        contentAlignment = Alignment.TopCenter
    ) {
        // ... (rest of the logic remains the same) ...
        val visibleCards = cards.take(stackDepth).reversed()

        visibleCards.forEachIndexed { reverseIndex, cardState ->
            val stackIndex = visibleCards.size - 1 - reverseIndex

            if (stackIndex == 0) {
                // The top, interactive card
                InteractiveProfileCard(
                    uiState = cardState,
                    onSwipe = onSwipe,
                    swipeProgress = swipeProgress
                )
            } else {
                // The passive, gray background cards
                PassiveProfileCard(
                    stackIndex = stackIndex,
                    uiState = cardState,
                    swipeProgress = swipeProgress.value,
                    isNextCard = stackIndex == 1
                )
            }
        }
    }
}
