package com.playground.diet.presentation.uistate

import com.playground.diet.domain.model.NonaCalendarDay
import com.playground.diet.domain.model.NonaLegend


data class NonaCalendarUiState(
    val currentMonthLabel: String = "October 2024",
    val days: List<NonaCalendarDay> = emptyList(),
    val legendItems: List<NonaLegend> = emptyList()
)

sealed interface NonaCalendarInteractionEvent {
    data object OnBackClick : NonaCalendarInteractionEvent
    data object OnPreviousMonthClick : NonaCalendarInteractionEvent
    data object OnDaySelected : NonaCalendarInteractionEvent // Simplified for preview
    data object OnClearMealsClick : NonaCalendarInteractionEvent
}
