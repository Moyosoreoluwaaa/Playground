package com.playground.healwise.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.playground.healwise.domain.model.PatientStats

@Composable
fun PatientStatsCard(
    modifier: Modifier = Modifier,
    stats: PatientStats,
    onDetailsClick: () -> Unit
) {
    Card(
        onClick = onDetailsClick, // Card click is the detail action
        modifier = modifier.height(150.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD9EAD3), // Light, pleasant green background
            contentColor = Color(0xFF333333) // Dark text for contrast
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Section: Title and Total Count
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stats.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stats.totalCountFormatted, // DERIVED from groups
                    style = MaterialTheme.typography.displayLarge,
                )
            }

            // Right Section: Venn Diagram
            VennDiagram(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                groupA = stats.groupA,
                groupB = stats.groupB,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
