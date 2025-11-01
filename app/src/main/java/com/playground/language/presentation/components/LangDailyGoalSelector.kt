package com.playground.language.presentation.components

// Using android R for placeholder drawables
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.playground.language.LangDailyGoal


// Assuming LangSpacing is 16.dp and LangPrimaryButton is defined elsewhere.

@Composable
fun LangDailyGoalSelector(
    goals: List<LangDailyGoal>,
    selectedGoal: LangDailyGoal,
    onGoalSelected: (LangDailyGoal) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = goals.indexOf(selectedGoal)
    val sliderPosition = selectedIndex.toFloat()

    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = sliderPosition,
            onValueChange = { newPosition ->
                val nearestIndex = newPosition.toInt().coerceIn(0, goals.lastIndex)
                onGoalSelected(goals[nearestIndex])
            },
            steps = goals.size - 2, // Steps are size - 2 for total slots - 1
            valueRange = 0f..(goals.lastIndex.toFloat()),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = LangSpacing / 2),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            goals.forEach { goal ->
                Text(
                    text = goal.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (goal == selectedGoal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}
