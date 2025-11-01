package com.playground.planner.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.playground.planner.domain.model.TaskTag

@Composable
fun TaskPillChip(
    tag: TaskTag
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = tag.color.copy(alpha = 0.2f),
        contentColor = tag.color,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            tag.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
