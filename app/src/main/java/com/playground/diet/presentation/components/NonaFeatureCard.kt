package com.playground.diet.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playground.diet.domain.model.NonaFeature
import com.playground.diet.domain.model.NonaFeatureType

@Composable
fun NonaFeatureCard(
    feature: NonaFeature,
    onClick: (NonaFeatureType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick(feature.type) },
        modifier = modifier
            .width(100.dp)
            .height(110.dp),
        colors = CardDefaults.cardColors(containerColor = feature.color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.label,
                tint = Color.Black
            )
            Text(
                text = feature.label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.Black
            )
        }
    }
}
