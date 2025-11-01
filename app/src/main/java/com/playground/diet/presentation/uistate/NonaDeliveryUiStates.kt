package com.playground.diet.presentation.uistate

import com.playground.diet.domain.model.NonaPreference
import com.playground.diet.domain.model.NonaTimeSlot

data class NonaDeliveryUiState(
    val steps: List<String> = listOf("Select Plans", "Delivery Details", "Payment"),
    val currentStepIndex: Int = 1, // "Delivery Details"
    val startDateText: String = "16 March 24",
    val deliverySlots: List<NonaTimeSlot> = emptyList(),
    val selectedSlotId: String = "7AM_11AM",
    val dislikes: List<NonaPreference> = emptyList(),
    val selectedDislikeIds: Set<String> = setOf("tree_nuts", "peanuts", "dairy_products"),
)

sealed interface NonaDeliveryInteractionEvent {
    data object OnBackClick : NonaDeliveryInteractionEvent
    data object OnARClick : NonaDeliveryInteractionEvent
    data object OnStartDateClick : NonaDeliveryInteractionEvent
    data class OnSlotSelected(val slotId: String) : NonaDeliveryInteractionEvent
    data class OnDislikeToggled(val preferenceId: String, val isSelected: Boolean) : NonaDeliveryInteractionEvent
    data object OnProceedClick : NonaDeliveryInteractionEvent
}
