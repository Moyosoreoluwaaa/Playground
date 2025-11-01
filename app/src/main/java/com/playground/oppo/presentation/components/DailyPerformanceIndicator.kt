package com.playground.oppo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.playground.oppo.domain.model.DayOfWeek
import com.playground.oppo.domain.model.DayPerformanceData

@Composable
fun DailyPerformanceIndicator(
    data: List<DayPerformanceData>,
    onDaySelected: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Daily Blocks Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            data.forEach { dayData ->
                Box(
                    modifier = Modifier
                        .weight(dayData.weight)
                        .height(25.dp)
                        .clip(RoundedCornerShape(100))
                        .background(dayData.color)
                        .clickable { onDaySelected(dayData.day) },
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        // Day Labels Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            data.forEach { dayData ->
                Text(
                    text = dayData.day.name.first().toString(), // S, M, T, W...
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(dayData.weight)
                )
            }
        }
    }
}
