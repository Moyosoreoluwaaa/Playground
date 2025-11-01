package com.playground.oppo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playground.oppo.domain.model.ActivityItemData

@Composable
fun ActivityCard(
    activityData: ActivityItemData,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth().clickable { onClick(activityData.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon (Stub)
            Box(
                modifier = Modifier.size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(activityData.iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for custom graphic (e.g., Image or Icon)
                Text("Icon", color = if (activityData.iconBackgroundColor == Color(0xFFFF4500)) Color.White else Color.Black)
            }

            Spacer(Modifier.width(16.dp))

            // Text Metrics
            Column(modifier = Modifier.weight(1f)) {
                Text(activityData.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row {
                    Text(activityData.percentageMetric, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text("\u223C", style = MaterialTheme.typography.bodyMedium, color = Color.Gray) // Tilde separator
                    Spacer(Modifier.width(4.dp))
                    Text(activityData.monetaryMetric, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                }
            }

            // Timestamp
            Text(activityData.timestamp, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}
