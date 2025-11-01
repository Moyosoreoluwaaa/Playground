package com.playground.planner.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.planner.domain.model.PlannerIntervalType
import com.playground.planner.domain.model.PlannerTask
import com.playground.planner.domain.model.PlannerViewType
import com.playground.planner.presentation.components.PlannerDateNavigator
import com.playground.planner.presentation.components.PlannerProjectHeader
import com.playground.planner.presentation.components.PlannerSegmentedFilterCard
import com.playground.planner.presentation.components.PlannerTaskActionRow
import com.playground.planner.presentation.components.PlannerTaskListCard
import com.playground.planner.presentation.components.PlannerTaskViewTabs
import com.playground.planner.presentation.components.PlannerTopBar
import com.playground.planner.presentation.uistate.PlannerUiState

// region Screen

@Composable
fun PlannerScreenContent(
    uiState: PlannerUiState,
    onProjectSelectorClicked: () -> Unit = {},
    onShareClicked: () -> Unit = {},
    onAddTaskClicked: () -> Unit = {},
    onAddTaskDropdownClicked: () -> Unit = {},
    onFilterSelected: (PlannerIntervalType) -> Unit = {},
    onDatePrevious: () -> Unit = {},
    onDateNext: () -> Unit = {},
    onTaskClicked: (PlannerTask) -> Unit = {},
    onTaskMenuClicked: (PlannerTask) -> Unit = {},
    onTabSelected: (PlannerViewType) -> Unit = {}
) {
    Scaffold(
        topBar = {
            PlannerTopBar(
                projectName = uiState.projectName,
                notificationCount = uiState.notificationCount,
                avatarUrl = uiState.avatarUrl
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize() // The Column must fill the available size
        ) {
            // --- FIXED (NON-SCROLLING) CONTENT ---

            // 1. PROJECT HEADER
            PlannerProjectHeader(
                projectName = uiState.projectName,
                onProjectSelectorClicked = onProjectSelectorClicked,
                onShareClicked = onShareClicked
            )

            // 2. VIEW TABS
            PlannerTaskViewTabs(
                currentView = uiState.currentView,
                onTabSelected = onTabSelected
            )

            // 3. TASK ACTION ROW
            PlannerTaskActionRow(
                onAddTaskClicked = onAddTaskClicked,
                onAddTaskDropdownClicked = onAddTaskDropdownClicked
            )

            // 4. DATE NAVIGATOR
            PlannerDateNavigator(
                currentLabel = uiState.currentNavigationLabel,
                onPreviousClicked = onDatePrevious,
                onNextClicked = onDateNext
            )

            // 5. SEGMENTED FILTER CARD
            PlannerSegmentedFilterCard(
                currentFilter = uiState.currentSegmentFilter,
                onFilterSelected = onFilterSelected
            )

            // HORIZONTAL DIVIDER
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            Spacer(Modifier.height(8.dp))

            // --- SCROLLABLE CONTENT ---

            // 6. TASK LIST CARD (This will now fill the remaining space and scroll internally)
            Box(
                // Use a Box with the weight modifier
                modifier = Modifier.weight(1f)
            ) {
                PlannerTaskListCard(
                    uiState = uiState,
                    onTaskClicked = onTaskClicked,
                    onTaskMenuClicked = onTaskMenuClicked
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Planner Screen Preview")
@Composable
private fun PlannerScreenPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
//        PlannerScreenContent(uiState = MockPlannerUiState)
    }
}
