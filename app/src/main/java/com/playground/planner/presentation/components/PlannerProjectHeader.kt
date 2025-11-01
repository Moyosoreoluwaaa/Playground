package com.playground.planner.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun PlannerProjectHeader(
    projectName: String,
    onProjectSelectorClicked: () -> Unit,
    onShareClicked: () -> Unit
) {
    // ... (PlannerProjectHeader implementation - Unchanged)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onProjectSelectorClicked) {
            Icon(Default.CalendarViewWeek, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text(projectName, style = MaterialTheme.typography.titleMedium)
            Icon(Default.KeyboardArrowDown, contentDescription = null)
        }

        Button(onClick = onShareClicked,
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Icon(Default.Share, contentDescription = null, Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Share")
        }
    }
}
