package com.playground.oppo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.playground.oppo.domain.model.PrimaryMetricCardData
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider

@Composable
fun OppoMetricCard(
    metricData: PrimaryMetricCardData,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onActionClick: (() -> Unit)? = null
) {
    val cardClick: () -> Unit =
        if (metricData.id == "sales" && onActionClick != null) onActionClick else {
            { onClick(metricData.id) }
        }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(0.dp))
            .background(metricData.backgroundColor)
            .clickable(onClick = cardClick)
            .padding(16.dp)
    ) {
        // Top Right Icon (e.g., Folder/Bag)
        if (metricData.iconRes != null) {
            Text(
                "[\uF0B1]",
                color = metricData.textColor.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }

        // --- Dedicated Content Composable ---
        if (metricData.id == "sales") {
            OverallSalesCardContent(metricData = metricData)
        } else {
            OrganicReachCardContent(metricData = metricData)
        }
        // ------------------------------------

        // Bottom Right Action Link (Overall Sales)
        if (metricData.actionIconRes != null) {
            Text(
                "\u2197", style = MaterialTheme.typography.headlineSmall,
                color = metricData.textColor,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

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

@Composable
private fun OverallSalesCardContent(metricData: PrimaryMetricCardData) {
    Column(
//        modifier = Modifier.align(Alignment.BottomStart)
    ) {
        // 1. HORIZONTAL DIVIDER
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = DividerDefaults.Thickness, color = metricData.textColor.copy(alpha = 0.3f)
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
