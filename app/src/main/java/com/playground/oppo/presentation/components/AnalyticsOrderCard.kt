package com.playground.oppo.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playground.oppo.domain.model.AnalyticsCardData
import com.playground.oppo.domain.model.DayOfWeek

@Composable
fun AnalyticsOrderCard(
    cardData: AnalyticsCardData,
    onDaySelected: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth().clickable { /* onCardClick event */ },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(cardData.title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(cardData.metricValue, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))

            // Change Indicator
            val changeColor = if (cardData.changeIsPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
            Text(
                text = cardData.changeValue,
                style = MaterialTheme.typography.titleSmall,
                color = changeColor,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(24.dp))

            // Daily Performance Indicator
            DailyPerformanceIndicator(data = cardData.dailyPerformance, onDaySelected = onDaySelected)
        }
    }
}
