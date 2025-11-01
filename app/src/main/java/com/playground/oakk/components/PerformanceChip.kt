package com.playground.oakk.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.oakk.OakkTheme
import java.util.Locale
import kotlin.math.abs

/**
 * A small chip to display asset performance (percentage change).
 * Changes color and icon based on whether the change is positive or negative.
 *
 * @param percentageChange The numerical value of the percentage change.
 * @param modifier The modifier to be applied to the component.
 */
@Composable
fun PerformanceChip(
    percentageChange: Double,
    modifier: Modifier = Modifier
) {
    val isPositive = percentageChange >= 0
    val icon = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
    val color = if (isPositive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
    val containerColor = color.copy(alpha = 0.1f)
    val contentDescription = if (isPositive) "Trending up" else "Trending down"

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = String.format(Locale.US, "%.2f%%", abs(percentageChange)),
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F3)
@Composable
private fun PerformanceChipPreview() {
    OakkTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PerformanceChip(percentageChange = 1.42)
            PerformanceChip(percentageChange = -7.13)
            PerformanceChip(percentageChange = 0.0)
        }
    }
}
