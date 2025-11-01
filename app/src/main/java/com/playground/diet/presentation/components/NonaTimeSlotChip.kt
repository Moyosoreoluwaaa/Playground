package com.playground.diet.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playground.diet.domain.model.NonaTimeSlot
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding
import com.playground.diet.presentation.theme.NonaColorBlueCyan

@Composable
fun NonaTimeSlotChip(
    slot: NonaTimeSlot,
    isSelected: Boolean,
    onClick: (String) -> Unit
) {
    val containerColor = if (isSelected) NonaColorBlueCyan else Color.White
    val textColor = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.7f)
    val borderColor = if (isSelected) Color.Transparent else Color.Black.copy(alpha = 0.2f)

    Surface(
        onClick = { onClick(slot.id) },
        shape = RoundedCornerShape(percent = 50),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.animateContentSize(animationSpec = spring())
    ) {
        Text(
            text = slot.label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = textColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}
