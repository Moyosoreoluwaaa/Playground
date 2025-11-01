package com.playground.planner.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.playground.planner.domain.model.PlannerPriority
import com.playground.planner.domain.model.PlannerTask

@Composable
fun PlannerTaskItem(
    task: PlannerTask,
    onTaskClicked: (PlannerTask) -> Unit,
    onTaskMenuClicked: (PlannerTask) -> Unit
) {
    // Wrapped in PlannerCard for the requested border/card style
    PlannerCard(
//        onClick = { onTaskClicked(task) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val highPriorityColor = Color(0xFF9E477D) // Mock color
        val lowPriorityColor = Color(0xFF6A65D1) // Mock color
        val onlineStatusColor = Color(0xFF00BFA5) // Mock color

        Column(Modifier.padding(16.dp)) {
            // ... (Task Item details implementation - Unchanged)
            Row(verticalAlignment = Alignment.Top) {
                Icon(task.typeIcon, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(16.dp))
                Text(
                    task.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = { onTaskMenuClicked(task) }) {
                    Icon(Default.MoreVert, contentDescription = "Task options")
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                PlannerTaskTag(
                    text = "${task.priority.name.replaceFirstChar { it.titlecase() }} Priority",
                    tagColor = if (task.priority == PlannerPriority.HIGH) highPriorityColor else lowPriorityColor
                )
                PlannerTaskTag(
                    text = task.statusTag,
                    tagColor = if (task.isOnline) onlineStatusColor else MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    task.timeRemaining,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.weight(1f))

                // Link Count
                Icon(
                    Default.Link,
                    contentDescription = "Links attached: ${task.linkCount}",
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    task.linkCount.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(end = 16.dp)
                )

                // Comment Count
                Icon(
                    Icons.AutoMirrored.Filled.Message,
                    contentDescription = "Comments/Subtasks: ${task.commentCount}",
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    task.commentCount.toString(),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
