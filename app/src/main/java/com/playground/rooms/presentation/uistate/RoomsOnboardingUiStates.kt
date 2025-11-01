package com.playground.rooms.presentation.uistate

import com.playground.rooms.domain.model.OnboardingSlide
import com.playground.rooms.domain.model.OnboardingSlideContent

data class OnboardingUiState(
    val currentPage: OnboardingSlide = OnboardingSlide.CONNECT,
    val slides: List<OnboardingSlideContent> = emptyList(),
    val isSkipButtonVisible: Boolean = true,
    val isProcessing: Boolean = false
)

sealed class OnboardingEvent {
    data object NextClicked : OnboardingEvent()
    data object SkipClicked : OnboardingEvent()
    data class PageSwiped(val index: Int) : OnboardingEvent()
    data object ProceedClicked : OnboardingEvent() // Final action
}
