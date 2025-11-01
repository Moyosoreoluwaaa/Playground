package com.playground.oppo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.playground.oppo.domain.model.CompositeBlockData

@Composable
fun OppoCompositeBlock(
    compositeData: CompositeBlockData,
    onSponsoredClick: () -> Unit,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Fixed height to match design
            .clip(RoundedCornerShape(0.dp))
            .background(compositeData.backgroundColor)
            .clickable { onClick(compositeData.id) }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Metric
        Text(
            text = compositeData.mainValue,
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 60.sp),
            fontWeight = FontWeight.Bold,
            color = compositeData.textColor,
            modifier = Modifier.padding(end = 8.dp)
        )
        // Separator
        Text("/", style = MaterialTheme.typography.headlineMedium, color = compositeData.textColor.copy(alpha = 0.5f))
        Spacer(Modifier.width(16.dp))

        // Right Details
        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Text(compositeData.detailLine1, style = MaterialTheme.typography.titleMedium, color = compositeData.textColor)
            Text(compositeData.detailLine2, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = compositeData.textColor)
            Text(
                compositeData.sponsoredText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary, // Using primary for link color
                modifier = Modifier.clickable(onClick = onSponsoredClick)
            )
        }
    }
}
