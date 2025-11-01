package com.playground.language.presentation.components

// Using android R for placeholder drawables
import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.language.domain.LangDashboardUiEvent
import com.playground.language.domain.LangDashboardUiState
import com.playground.language.domain.LangGamifiedItemCategory
import com.playground.language.domain.LangGamifiedMetricItem
import com.playground.language.domain.LangMetric
import com.playground.language.presentation.LangContinueLearningCard
import com.playground.language.presentation.LangGamifiedMetricItemCard
import com.playground.language.presentation.LangLessonProgressIndicator
import com.playground.language.presentation.LangMetricCard
import com.playground.language.presentation.LangProgressIndicatorDots

@Preview(
    showBackground = true,
    name = "LangDashboardScreen Preview"
)
@Composable
fun LangDashboardScreenPreview() {
    // Create realistic sample data for the preview matching the data class definitions
    val sampleState = LangDashboardUiState(
        userName = "Maria",
        lessonQuote = "A different language is a different vision of life.",
        lessonProgress = 0.65f,
        lessonSteps = 5,
        currentStepIndex = 2,
        currentModule = "Unit 3: Conversations",
        currentLesson = "Ordering Food & Drinks",
        langMetrics = listOf(
            LangMetric(
                id = "streak",
                label = "Streak",
                value = "5 days",
                iconResId = R.drawable.ic_dialog_info // Placeholder icon
            ),
            LangMetric(
                id = "xp",
                label = "XP",
                value = "1,250",
                iconResId = R.drawable.ic_dialog_dialer // Placeholder icon
            )
        ),
        langGamifiedItems = listOf(
            LangGamifiedMetricItem(
                id = "perfect_lesson",
                title = "Perfect Lesson",
                iconResId = R.drawable.star_on, // Placeholder icon
                category = LangGamifiedItemCategory.GRAMMAR
            ),
            LangGamifiedMetricItem(
                id = "time_trial",
                title = "Time Trial",
                iconResId = R.drawable.ic_media_play, // Placeholder icon
                category = LangGamifiedItemCategory.TRAVEL
            ),
            LangGamifiedMetricItem(
                id = "flawless_pronunciation",
                title = "Flawless Pronunciation",
                iconResId = R.drawable.ic_btn_speak_now, // Placeholder icon
                category = LangGamifiedItemCategory.VOCABULARY
            )
        )
    )

    // Call your screen composable with the sample data
    LangDashboardScreen(
        state = sampleState,
        onEvent = {} // Events can be empty for preview purposes
    )
}

@Composable
fun LangTopGreeting(
    state: LangDashboardUiState,
    onEvent: (LangDashboardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = LangSpacing / 2, bottom = LangSpacing),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hello, ${state.userName}!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = state.lessonQuote,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Profile Icon Button
        IconButton(
            onClick = { onEvent(LangDashboardUiEvent.OnProfileClick) },
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                Icons.Filled.Person,
                contentDescription = "Open profile and settings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LangDashboardScreen(
    state: LangDashboardUiState,
    onEvent: (LangDashboardUiEvent) -> Unit
) {
    val scrollState = rememberScrollState()

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow, // Very light background
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LangSpacing)
                .verticalScroll(scrollState)
        ) {
            // Placeholder for Status Bar and initial padding
            Spacer(Modifier.height(32.dp)) 

            // 1. Top Greeting
            LangTopGreeting(state, onEvent)

            // 2. Detail Lesson & Progress
            Text(
                text = "Detail Lesson!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            LangLessonProgressIndicator(
                progress = state.lessonProgress,
                modifier = Modifier.clickable { onEvent(LangDashboardUiEvent.OnLessonProgressClick) }
            )
            LangProgressIndicatorDots(
                count = state.lessonSteps,
                activeIndex = state.currentStepIndex
            )

            Spacer(Modifier.height(LangSpacing * 2))

            // 3. Continue Journey Card
            LangContinueLearningCard(
                module = state.currentModule,
                lesson = state.currentLesson,
                onContinueLearningClick = { onEvent(LangDashboardUiEvent.OnContinueLearningClick) }
            )

            Spacer(Modifier.height(LangSpacing * 2))

            // 4. Your Progress Section
            Text(
                text = "Your Progress",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = LangSpacing)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LangSpacing)
            ) {
                state.langMetrics.forEach { metric ->
                    LangMetricCard(metric = metric, onClick = { onEvent(LangDashboardUiEvent.OnMetricClick(it)) })
                }
            }

            Spacer(Modifier.height(LangSpacing * 2))

            // 5. Gamified Metric Item Section
            Text(
                text = "Gamified Metric Item",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = LangSpacing)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(LangSpacing),
                contentPadding = PaddingValues(end = LangSpacing) // To show content off-screen
            ) {
                items(state.langGamifiedItems) { item ->
                    LangGamifiedMetricItemCard(item = item, onClick = { onEvent(LangDashboardUiEvent.OnGamifiedItemClick(it)) })
                }
            }

            Spacer(Modifier.height(LangSpacing * 2))
        }
    }
}