package com.playground.diet.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.playground.diet.domain.model.NonaCalendarDay
import com.playground.diet.domain.model.NonaDayState
import com.playground.diet.presentation.theme.NonaColorBlueCyan
import com.playground.diet.presentation.theme.NonaColorPurple
import com.playground.diet.presentation.theme.NonaColorYellowGreen

@Composable
fun NonaCalendarDayCell(
    day: NonaCalendarDay,
    onClick: (Long) -> Unit
) {
    val dayColor = when (day.state) {
        NonaDayState.AUTO_SELECTED -> NonaColorYellowGreen
        NonaDayState.FUTURE_MEAL -> NonaColorBlueCyan
        else -> Color.Transparent
    }

    val contentColor = when (day.state) {
        NonaDayState.INACTIVE -> Color.Black.copy(alpha = 0.3f)
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f) // Ensures a square cell
            .clip(RoundedCornerShape(8.dp))
            .background(dayColor)
            .clickable(enabled = day.state != NonaDayState.INACTIVE) { onClick(day.id) },
        contentAlignment = Alignment.Center
    ) {
        if (day.state == NonaDayState.SELECTED_TODAY) {
            // Purple circle indicator for today
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(NonaColorPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.date.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = NonaColorPurple
                )
            }
        } else {
            Text(
                text = day.date.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
        }
    }
}
