package com.playground.diet.presentation.uistate

import com.playground.diet.domain.model.NonaFeature
import com.playground.diet.domain.model.NonaFeatureType
import com.playground.diet.domain.model.NonaNavRoute

data class NonaUiState(
    val features: List<NonaFeature> = emptyList(),
    val selectedNavRoute: NonaNavRoute = NonaNavRoute.HOME,
    val notificationCount: Int = 2,
)
sealed interface NonaInteractionEvent {
    data object OnLoginClick : NonaInteractionEvent
    data object OnDieticianLinkClick : NonaInteractionEvent
    data object OnMenuClick : NonaInteractionEvent
    data object OnCalculateClick : NonaInteractionEvent
    data class OnFeatureSelected(val type: NonaFeatureType) : NonaInteractionEvent
    data class OnNavSelected(val route: NonaNavRoute) : NonaInteractionEvent
}
