package com.playground.oppo.presentation.components

import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.oppo.domain.model.PrimaryMetricCardData

@Composable
private fun OrganicReachCardContent(metricData: PrimaryMetricCardData) {
    // Main Metric (Bottom Left)
    if (metricData.primaryMetric.isNotEmpty()) {
        Text(
            text = metricData.primaryMetric,
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 40.sp),
            fontWeight = FontWeight.Bold,
            color = metricData.textColor,
//            textAlign = Alignment.BottomStart

        )
    }

    // Description/Title (Offset below the metric - The original, intended behavior)
    if (metricData.primaryMetric.isNotEmpty()) {
        Text(
            text = metricData.description,
            style = MaterialTheme.typography.titleMedium,
            color = metricData.textColor.copy(alpha = 0.7f),
            modifier = Modifier
                .offset(y = 50.dp) // Pushes description below the metric
        )
    }
}
