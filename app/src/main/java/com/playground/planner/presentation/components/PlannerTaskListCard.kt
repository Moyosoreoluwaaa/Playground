package com.playground.planner.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.playground.planner.domain.model.PlannerTask
import com.playground.planner.presentation.uistate.PlannerUiState

@Composable
fun PlannerTaskListCard(
    uiState: PlannerUiState,
    onTaskClicked: (PlannerTask) -> Unit,
    onTaskMenuClicked: (PlannerTask) -> Unit
) {
    PlannerCard(
        // The weight modifier will be applied by the parent, not here.
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // This LazyColumn will now scroll within the bounds given by the parent.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            // userScrollEnabled is true by default, so you can omit it.
        ) {
            uiState.taskSections.forEach { section ->
                item(key = "header_${section.title}") { // Added a key for the header
                    // Task Section Header
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            section.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${section.tasks.size}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                items(section.tasks, key = { it.id }) { task ->
                    PlannerTaskItem(
                        task = task,
                        onTaskClicked = onTaskClicked,
                        onTaskMenuClicked = onTaskMenuClicked
                    )
                }
            }
        }
    }
}
