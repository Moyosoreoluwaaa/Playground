package com.playground.oppo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playground.oppo.domain.model.EarningSource

@Composable
fun EarningSourceCard(source: EarningSource, onClick: (EarningSource) -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(source) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Block
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(source.backgroundColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for actual custom icon
                Text("Icon", color = source.iconColor, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))

            // Text Details
            Column(Modifier.weight(1f)) {
                Text(source.title, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val changeColor = if (source.isPositiveChange) Color(0xFF4CAF50) else Color(0xFFF44336)
                    Text(
                        text = if (source.isPositiveChange) "^\uFE0E" else "v\uFE0E",
                        color = changeColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${"%.2f".format(source.percentageChange)}%",
                        color = changeColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "~ $${"%,.0f".format(source.amount)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Timestamp
            Text(
                source.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
