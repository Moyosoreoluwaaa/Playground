package com.playground.language.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.language.data.LangBadge
import com.playground.language.data.LangBadgeIcon
import com.playground.language.data.LangStreakDay
import com.playground.language.presentation.LangPrimaryButton
import com.playground.language.presentation.components.LangBadgeCard
import com.playground.language.presentation.components.LangSpacing
import com.playground.language.presentation.components.LangStreakCalendar
import com.playground.language.presentation.uistate.LangAchievementsUiEvent
import com.playground.language.presentation.uistate.LangAchievementsUiState

@Composable
fun LangAchievementsScreen(
    state: LangAchievementsUiState,
    onEvent: (LangAchievementsUiEvent) -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(horizontal = LangSpacing)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onEvent(LangAchievementsUiEvent.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = "Streak & Achievements!",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "You study ${state.studyGoalMinutes} minutes a day",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = LangSpacing),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Current Streak Section
                Spacer(Modifier.height(LangSpacing))
                Divider(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    thickness = 2.dp,
                    modifier = Modifier.width(100.dp)
                )

                Spacer(Modifier.height(LangSpacing))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Current Streak:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${state.currentStreakDays} Days!",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Brain Icon (Placeholder for LangStreakCardVisual)
                    Icon(
                        Icons.Filled.Psychology,
                        contentDescription = "Streak visualization",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }
                Spacer(Modifier.height(LangSpacing * 2))

                // Streak Calendar
                LangStreakCalendar(days = state.streakCalendar)

                Spacer(Modifier.height(LangSpacing * 2))

                // Earned Badges Section
                Text(
                    text = "Earned Badges",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = LangSpacing)
                )

                if (state.earnedBadges.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp), // Constrain height for scrolling within the main column
                        horizontalArrangement = Arrangement.spacedBy(LangSpacing),
                        verticalArrangement = Arrangement.spacedBy(LangSpacing),
                        userScrollEnabled = false // Disable grid scroll since the main column scrolls
                    ) {
                        items(state.earnedBadges) { badge ->
                            LangBadgeCard(
                                badge = badge,
                                onClick = { onEvent(LangAchievementsUiEvent.OnBadgeClick(it)) })
                        }
                    }
                } else {
                    Text(
                        "Start earning badges now!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.weight(1f)) // Push button to bottom

                // Continue Learning Button
                LangPrimaryButton(
                    text = "Continue Learning",
                    onClick = { onEvent(LangAchievementsUiEvent.OnContinueLearningClick) },
                    modifier = Modifier.padding(vertical = LangSpacing)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LangAchievementsScreenPreview() {
    val mockBadges = listOf(
        LangBadge("wm", "Word Master", LangBadgeIcon.STAR),
        LangBadge("gg", "Grammar Guru", LangBadgeIcon.SPEAKER),
        LangBadge("ll", "Listening Legend", LangBadgeIcon.TROPHY),
        LangBadge("cc", "Consistent Climber", LangBadgeIcon.MOUNTAIN)
    )
    val mockCalendar = listOf(
        LangStreakDay(1, true),
        LangStreakDay(1, true),
        LangStreakDay(2, true),
        LangStreakDay(3, true),
        LangStreakDay(6, true),
        LangStreakDay(7, true),
        LangStreakDay(8, false),
        LangStreakDay(5, false) // Day numbers are irregular, matching the image.
    )

    val mockState = LangAchievementsUiState(
        studyGoalMinutes = 30,
        currentStreakDays = 7,
        streakCalendar = mockCalendar,
        earnedBadges = mockBadges
    )

    LangAchievementsScreen(
        state = mockState,
        onEvent = {}
    )
}