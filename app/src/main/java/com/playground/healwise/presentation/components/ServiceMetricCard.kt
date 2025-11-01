package com.playground.healwise.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.healwise.domain.model.ServiceMetricCardData

@Composable
fun ServiceMetricCard(
    cardData: ServiceMetricCardData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = cardData.backgroundColor),
        modifier = modifier.height(180.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    cardData.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
                Icon(cardData.leadingIcon, contentDescription = null, tint = Color.White)
            }
            Spacer(Modifier.height(8.dp))
            // Metric Row
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    cardData.metricValue,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp),
                    color = Color.Black // Ensure high contrast
                )
                Icon(
                    cardData.metricTrailingIcon,
                    contentDescription = null,
                    tint = Color.Black, // Ensure high contrast
                    modifier = Modifier
                        .padding(start = 8.dp, bottom = 8.dp)
                        .size(24.dp)
                )
            }
            // Bottom Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    cardData.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black // Ensure high contrast
                )
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "View details",
                    tint = Color.Black // Ensure high contrast
                )
            }
        }
    }
}
