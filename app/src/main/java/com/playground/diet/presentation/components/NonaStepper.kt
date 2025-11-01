package com.playground.diet.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.playground.diet.domain.model.NonaStepperStatus

@Composable
fun NonaStepper(
    currentStepIndex: Int,
    steps: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, title ->
            val status = when {
                index < currentStepIndex -> NonaStepperStatus.COMPLETED
                index == currentStepIndex -> NonaStepperStatus.CURRENT
                else -> NonaStepperStatus.PENDING
            }

            val isLast = index == steps.lastIndex

            NonaStepItem(title = title, status = status)

            if (!isLast) {
                Divider(
                    color = if (status == NonaStepperStatus.COMPLETED) Color.Black else Color.Black.copy(alpha = 0.3f),
                    thickness = 2.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
