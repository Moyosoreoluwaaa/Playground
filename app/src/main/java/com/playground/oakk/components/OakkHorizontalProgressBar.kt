package com.playground.oakk.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.oakk.OakkTheme
import com.playground.oakk.uistates.OakkAssetCategory

// Reusable composable for a single horizontal progress bar
@Composable
fun OakkHorizontalProgressBar(
    percentage: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(color.copy(alpha = 0.2f)) // Light background track
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(percentage / 100f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
    }
}


@Composable
fun OakkAssetDistributionCard(
    categories: List<OakkAssetCategory>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(24.dp)) {
            Text(
                text = "DISTRIBUTION OF ASSETS",
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(16.dp))

            // Single Horizontal Bar Chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .semantics { contentDescription = "Asset distribution bar chart" }
            ) {
                var currentX = 0f
                categories.forEach { category ->
                    val percentage = category.percentage.removeSuffix("%").toFloatOrNull() ?: 0f
                    val segmentWidth = size.width * (percentage / 100f)
                    drawRect(
                        color = category.color,
                        topLeft = Offset(currentX, 0f),
                        size = androidx.compose.ui.geometry.Size(segmentWidth, size.height)
                    )
                    currentX += segmentWidth
                }
            }

            Spacer(Modifier.height(16.dp))

            // Legend for the chart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Using a chunked list to create two columns
                categories.chunked(3).forEach { rowCategories ->
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowCategories.forEach { category ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(category.color)
                                        .semantics { contentDescription = "${category.name} color swatch" }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = category.percentage,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}