package com.playground.planner.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.playground.planner.domain.model.TaskTab

@Composable
fun CommentTabs(
    currentTab: TaskTab,
    onTabSelected: (TaskTab) -> Unit
) {
    // Custom segmented control (rounded pill style)
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                RoundedCornerShape(50)
            )
            .padding(4.dp)
    ) {
        TaskTab.entries.forEach { tab ->
            val isSelected = currentTab == tab
            TextButton(
                onClick = { onTabSelected(tab) },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(0.dp)
            ) {
                Text(tab.name.replaceFirstChar { it.titlecase() })
            }
        }
    }
}
