package com.playground.diet.domain.model

data class NonaCalendarDay(
    val id: Long, // Unique ID for event handling
    val date: Int,
    val state: NonaDayState,
    val isToday: Boolean = false
)
