package com.playground.diet.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playground.diet.domain.model.NonaStepperStatus

@Composable
fun NonaStepItem(title: String, status: NonaStepperStatus) {
    val circleColor = when (status) {
        NonaStepperStatus.COMPLETED, NonaStepperStatus.CURRENT -> Color.Black
        NonaStepperStatus.PENDING -> Color.White
    }
    val borderColor = if (status == NonaStepperStatus.PENDING) Color.Black.copy(alpha = 0.3f) else Color.Transparent
    val titleColor = if (status == NonaStepperStatus.PENDING) Color.Black.copy(alpha = 0.5f) else Color.Black

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(circleColor)
                .border(1.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            when (status) {
                NonaStepperStatus.COMPLETED -> Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                NonaStepperStatus.CURRENT -> Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color.White))
                NonaStepperStatus.PENDING -> Unit
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = titleColor,
            fontWeight = if (status == NonaStepperStatus.CURRENT) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
