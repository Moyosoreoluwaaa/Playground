package com.playground.language.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.language.data.LangAchievementMetric
import com.playground.language.data.LangMetricType
import com.playground.language.presentation.LangPrimaryButton
import com.playground.language.presentation.components.LangAchievementCard
import com.playground.language.presentation.components.LangSpacing
import com.playground.language.presentation.uistate.LangCompletionUiEvent
import com.playground.language.presentation.uistate.LangCompletionUiState

@Composable
fun LangCompletionScreen(
    state: LangCompletionUiState,
    onEvent: (LangCompletionUiEvent) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(LangSpacing * 2)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Push button to bottom
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 1. Header
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Lesson successfully completed",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Lesson Complete!",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Divider(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    thickness = 2.dp,
                    modifier = Modifier
                        .width(60.dp)
                        .padding(vertical = LangSpacing)
                )

                // 2. Achievement Area (Simplified Brain/XP)
                Icon(
                    Icons.Filled.VerifiedUser, // Placeholder for Brain Illustration
                    contentDescription = "Achievement illustration",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(120.dp)
                        .padding(LangSpacing)
                )
                Text(
                    text = "Awesome! +${state.xpGained} XP",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = LangSpacing * 2)
                )

                // 3. Metric Summary
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(LangSpacing)
                ) {
                    state.achievementMetrics.forEach { metric ->
                        val icon = when (metric.type) {
                            LangMetricType.STREAK -> Icons.Filled.Star
                            LangMetricType.TIME_SPENT -> Icons.Filled.History
                            LangMetricType.BADGE -> Icons.Filled.EmojiEvents
                        }
                        LangAchievementCard(icon = icon, text = metric.value)
                    }
                }
            }

            // 4. Continue Button (Pushed to bottom using SpaceBetween or Spacer(Modifier.weight(1f)))
            Spacer(Modifier.height(LangSpacing * 3)) // Ensure separation
            LangPrimaryButton(
                text = "Continue Learning",
                onClick = { onEvent(LangCompletionUiEvent.OnContinueLearningClick) },
                modifier = Modifier.padding(bottom = LangSpacing)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LangCompletionScreenPreview() {
    val metrics = listOf(
        LangAchievementMetric("streak", "Streak", "New Streak: 5 Days!", LangMetricType.STREAK),
        LangAchievementMetric("time", "Time", "Time Spent: 6 min", LangMetricType.TIME_SPENT),
        LangAchievementMetric("badge", "Badge", "New Badge: \"Word Master\"", LangMetricType.BADGE)
    )

    val mockState = LangCompletionUiState(
        xpGained = 50,
        achievementMetrics = metrics
    )

    LangCompletionScreen(
        state = mockState,
        onEvent = {}
    )
}