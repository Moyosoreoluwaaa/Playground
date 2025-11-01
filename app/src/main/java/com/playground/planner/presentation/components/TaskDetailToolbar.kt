package com.playground.planner.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.playground.planner.domain.model.TaskAction

@Composable
fun TaskDetailToolbar(
    isCompleted: Boolean,
    onCompletedToggled: (Boolean) -> Unit = {},
    onActionClicked: (TaskAction) -> Unit = {},
    onCloseClicked: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mark Completed Checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onCompletedToggled(!isCompleted) }
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = onCompletedToggled,
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
            Text(
                "Mark Completed",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(Modifier.weight(1f))

        // Action Icons
        IconButton(onClick = { onActionClicked(TaskAction.SHARE) }) {
            Icon(Icons.Default.Share, contentDescription = "Share task")
        }
        IconButton(onClick = { onActionClicked(TaskAction.LINK) }) {
            Icon(Icons.Default.AttachFile, contentDescription = "Attach file")
        }
        IconButton(onClick = { onActionClicked(TaskAction.EXPAND) }) {
            Icon(Icons.Default.ZoomOutMap, contentDescription = "Expand view")
        }
        IconButton(onClick = { onActionClicked(TaskAction.MORE) }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        IconButton(onClick = { onActionClicked(TaskAction.MOVE) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Move task")
        }

        Spacer(Modifier.width(8.dp))

        // Close Button
        IconButton(onClick = onCloseClicked) {
            Icon(Icons.Default.Close, contentDescription = "Close task details")
        }
    }
}
