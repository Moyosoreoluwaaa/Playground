package com.playground.oppo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.oppo.domain.model.MetricCardData

@Composable
fun OppoGenericMetricCard(
    metricData: MetricCardData,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f) // Ensures square shape
            .clip(RoundedCornerShape(0.dp)) // Sharp edges as per blueprint
            .background(metricData.backgroundColor)
            .clickable { onClick(metricData.id) }
            .padding(16.dp)
    ) {
        // Top Left: Description and Icon
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = metricData.description,
                    style = MaterialTheme.typography.labelMedium,
                    color = metricData.textColor.copy(alpha = 0.7f)
                )
                Spacer(Modifier.width(4.dp))
                // Placeholder for icon (e.g., ic_folder_bag)
                if (metricData.iconRes != null) {
                    Text("[\uF0B1]", color = metricData.textColor.copy(alpha = 0.7f))
                }
            }
        }

        // Bottom Left: Primary Metric
        Text(
            text = metricData.primaryMetric,
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp), // Adjusted custom size
            fontWeight = FontWeight.Bold,
            color = metricData.textColor,
            modifier = Modifier.align(Alignment.BottomStart)
        )

        // Bottom Right: Action Link (only for Overall Sales)
        if (metricData.actionIconRes != null) {
            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = metricData.description, // Repeated for visual reference
                    style = MaterialTheme.typography.labelMedium,
                    color = metricData.textColor.copy(alpha = 0.7f)
                )
                // Placeholder for action icon (e.g., ic_arrow_up_right)
                Text("\u2197", style = MaterialTheme.typography.headlineSmall, color = metricData.textColor)
            }
        }
    }
}
