package com.playground.oppo.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playground.oppo.domain.model.PrimaryMetricCardData

@Composable
private fun OverallSalesCardContent(metricData: PrimaryMetricCardData) {
    Column(
//        modifier = Modifier.align(Alignment.BottomStart)
    ) {
        // 1. HORIZONTAL DIVIDER
        Divider(
            color = metricData.textColor.copy(alpha = 0.3f),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Description/Title (Overall Sales text)
        Text(
            text = metricData.description,
            style = MaterialTheme.typography.labelMedium,
            color = metricData.textColor.copy(alpha = 0.7f)
        )

        // 3. SECONDARY METRIC (The Data for Overall Sales)
        if (metricData.secondaryMetric.isNotEmpty()) {
            Text(
                text = metricData.secondaryMetric,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = metricData.textColor
            )
        }
    }
}
