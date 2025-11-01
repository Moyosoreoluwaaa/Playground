package com.playground.diet.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.diet.domain.model.NonaCalendarDay
import com.playground.diet.domain.model.NonaDayState
import com.playground.diet.domain.model.NonaLegend
import com.playground.diet.domain.model.NonaLegendType
import com.playground.diet.presentation.components.NonaCalendarDayCell
import com.playground.diet.presentation.components.NonaClearButton
import com.playground.diet.presentation.components.NonaLegendItem
import com.playground.diet.presentation.components.NonaTopAppBar
import com.playground.diet.presentation.uistate.NonaCalendarInteractionEvent
import com.playground.diet.presentation.uistate.NonaCalendarUiState
import java.time.Instant

@Composable
fun NonaMealCalendarScreen(
    state: NonaCalendarUiState,
    onEvent: (NonaCalendarInteractionEvent) -> Unit
) {
    Scaffold(
        topBar = {
            NonaTopAppBar(
                title = "Meal Calendar",
                onBackClick = { onEvent(NonaCalendarInteractionEvent.OnBackClick) }
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant // Simulating the light grey background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surface) // White card background
                    .padding(24.dp)
                    .weight(1f) // Takes remaining space above button
            ) {
                // Calendar Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onEvent(NonaCalendarInteractionEvent.OnPreviousMonthClick) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
                    }
                    Text(
                        text = state.currentMonthLabel,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    IconButton(onClick = { onEvent(NonaCalendarInteractionEvent.OnDaySelected) }) { // Simplified interaction
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Day of Week Header
                val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))

                // Calendar Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    contentPadding = PaddingValues(0.dp),
                    userScrollEnabled = false,
                    modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp) // Max height for 6 rows
                ) {
                    items(state.days) { day ->
                        NonaCalendarDayCell(day = day, onClick = { onEvent(NonaCalendarInteractionEvent.OnDaySelected) })
                    }
                }
                Spacer(Modifier.height(32.dp))

                // Legend
                Text(
                    text = "Legend",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        state.legendItems.take(3).forEach { item ->
                            NonaLegendItem(icon = item.icon, label = item.label)
                        }
                    }
                    Column {
                        state.legendItems.takeLast(2).forEach { item ->
                            NonaLegendItem(icon = item.icon, label = item.label)
                        }
                    }
                }
            }

            // Clear Button (Sticky)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                NonaClearButton(onClick = { onEvent(NonaCalendarInteractionEvent.OnClearMealsClick) })
            }
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true)
@Composable
fun NonaMealCalendarScreenPreview() {
    val mockDays = mutableListOf<NonaCalendarDay>()

    // Add inactive days from previous month
    mockDays.add(NonaCalendarDay(0, 30, NonaDayState.INACTIVE))
    mockDays.add(NonaCalendarDay(0, 31, NonaDayState.INACTIVE))

    // Add current month days
    for (i in 1..31) {
        val state = when (i) {
            5 -> NonaDayState.SELECTED_TODAY
            7, 8 -> NonaDayState.AUTO_SELECTED
            17, 18, 19, 20, 21, 22, 23 -> NonaDayState.FUTURE_MEAL
            else -> NonaDayState.NONE
        }
        mockDays.add(NonaCalendarDay(Instant.now().epochSecond + i, i, state))
    }

    // Fill the grid to 42 cells (6 weeks)
    while (mockDays.size < 42) {
        mockDays.add(NonaCalendarDay(0, mockDays.size - 31, NonaDayState.INACTIVE))
    }

    val mockLegend = listOf(
        NonaLegend(NonaLegendType.SELECTED_MEALS, Icons.Default.CheckCircle, "Selected Meals", Color.Transparent),
        NonaLegend(NonaLegendType.AUTO_SELECTED, Icons.Default.Edit, "Auto Selected", Color.Transparent),
        NonaLegend(NonaLegendType.MEAL_LOCKED, Icons.Default.Lock, "Meal cannot change", Color.Transparent),
        NonaLegend(NonaLegendType.PAUSED_DAYS_1, Icons.Default.Pause, "Paused Days", Color.Transparent),
        NonaLegend(NonaLegendType.PAUSED_DAYS_2, Icons.Default.History, "Paused Days", Color.Transparent)
    )

    val mockState = NonaCalendarUiState(
        currentMonthLabel = "October 2024",
        days = mockDays,
        legendItems = mockLegend
    )

    MaterialTheme(
        colorScheme = lightColorScheme(
            surface = Color.White,
            surfaceVariant = Color(0xFFF0F0F0) // Light Grey background
        )
    ) {
        NonaMealCalendarScreen(state = mockState, onEvent = { /* NO-OP */ })
    }
}
