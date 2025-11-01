package com.playground.planner.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlannerCard(
    modifier: Modifier = Modifier,
//    onClick: () -> Unit,
    // FIX: Use ColumnScope for content to satisfy Card's internal structure
    content: @Composable ColumnScope.() -> Unit
) {
    // Re-introducing border/stroke as requested (using 10% outline opacity)
    val strokeColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.5f
            )
        ),
        modifier = modifier
            .border(1.dp, strokeColor, RoundedCornerShape(12.dp)),
        content = content
    )
}
