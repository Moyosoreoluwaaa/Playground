package com.playground.diet.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.playground.diet.domain.model.NonaPreference
import com.playground.diet.presentation.theme.NonaColorYellowGreen

@Composable
fun NonaPreferenceChip(
    preference: NonaPreference,
    isSelected: Boolean,
    onClick: (String, Boolean) -> Unit
) {
    val containerColor = if (isSelected) NonaColorYellowGreen else Color.White
    val borderColor = if (isSelected) Color.Transparent else Color.Black.copy(alpha = 0.2f)

    // Using FilterChip style but custom colors/shape
    Surface(
        onClick = { onClick(preference.id, !isSelected) },
        shape = RoundedCornerShape(percent = 50),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.animateContentSize(animationSpec = spring())
    ) {
        Text(
            text = preference.label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
