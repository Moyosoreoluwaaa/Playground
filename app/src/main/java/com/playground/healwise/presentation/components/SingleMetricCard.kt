package com.playground.healwise.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.healwise.domain.model.SingleMetricData
import com.playground.healwise.presentation.theme.DarkContrast

@Composable
fun SingleMetricCard(
    data: SingleMetricData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = data.backgroundColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    data.metricValue,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = DarkContrast
                )
                if (data.subMetricValue != null) {
                    Text(
                        data.subMetricValue,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = DarkContrast
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    data.description,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    color = DarkContrast
                )

                // Ellipsis/Action Indicator for details
                if (data.subMetricValue == null) {
                    Text("...", style = MaterialTheme.typography.titleLarge)
                } else {
                    // The occupancy card only has the ellipsis at the end, not a separate arrow
                }
            }

            // Icon and Optional Arrow (on the right)
            Column(horizontalAlignment = Alignment.End) {
                Icon(
                    data.icon,
                    contentDescription = data.description,
                    tint = DarkContrast,
                    modifier = Modifier.size(48.dp)
                )
                if (data.subMetricValue == null) {
                    // For Re-Admitted card
                    Spacer(Modifier.height(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "View details",
                        tint = DarkContrast,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
