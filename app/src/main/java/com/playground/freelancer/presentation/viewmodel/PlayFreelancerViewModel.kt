package com.playground.freelancer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.playground.freelancer.data.source.MockFreelancerData
import com.playground.freelancer.domain.model.PlayFreelancerSwipeDirection
import com.playground.freelancer.presentation.uistate.PlayFreelancerCardStackUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlayFreelancerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        PlayFreelancerCardStackUiState(
            cardQueue = MockFreelancerData.generateProfiles()
        )
    )
    val uiState: StateFlow<PlayFreelancerCardStackUiState> = _uiState.asStateFlow()

    fun swipeCard(direction: PlayFreelancerSwipeDirection) {
        _uiState.update { currentState ->
            if (currentState.cardQueue.isEmpty()) return@update currentState

            val swipedCard = currentState.cardQueue.first()
            currentState.copy(
                cardQueue = currentState.cardQueue.drop(1),
                historyStack = listOf(swipedCard) + currentState.historyStack
            )
        }
    }
}
