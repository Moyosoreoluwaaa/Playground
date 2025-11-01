package com.playground.language.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.language.LangDailyGoal
import com.playground.language.LangLanguageOption
import com.playground.language.presentation.LangPrimaryButton
import com.playground.language.presentation.components.LangDailyGoalSelector
import com.playground.language.presentation.components.LangLanguageOptionButton
import com.playground.language.presentation.components.LangSpacing
import com.playground.language.presentation.uistate.LangOnboardingUiEvent
import com.playground.language.presentation.uistate.LangOnboardingUiState

@Composable
fun LangOnboardingScreen(
    state: LangOnboardingUiState,
    onEvent: (LangOnboardingUiEvent) -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LangSpacing),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onEvent(LangOnboardingUiEvent.OnBackClick) }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                TextButton(onClick = { onEvent(LangOnboardingUiEvent.OnSkipClick) }) {
                    Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        bottomBar = {
            Column(modifier = Modifier.padding(LangSpacing)) {
                LangPrimaryButton(
                    text = "Start Learning!",
                    onClick = { onEvent(LangOnboardingUiEvent.OnStartLearningClick) },
//                    enabled = state.startLearningEnabled
                )
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = LangSpacing * 1.5f), // Extra padding for header alignment
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Header
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = state.headerText,
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = state.instructionText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                Spacer(Modifier.height(LangSpacing * 2))

                // 2. Illustration (Placeholder)
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Language,
                        contentDescription = "World and people illustration",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(120.dp)
                    )
                }

                Spacer(Modifier.height(LangSpacing * 2))

                // 3. Daily Goal Selector
                Text(
                    text = "Daily Goal",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = LangSpacing)
                )
                LangDailyGoalSelector(
                    goals = LangDailyGoal.allGoals(),
                    selectedGoal = state.selectedGoal,
                    onGoalSelected = { onEvent(LangOnboardingUiEvent.OnGoalSelected(it)) }
                )

                Spacer(Modifier.height(LangSpacing * 3))

                // 4. Language Selector
                Text(
                    text = "Choose your language",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = LangSpacing)
                )

                Column(verticalArrangement = Arrangement.spacedBy(LangSpacing / 2)) {
                    state.availableLanguages.forEach { lang ->
                        LangLanguageOptionButton(
                            option = lang,
                            isSelected = lang.code == state.selectedLanguageCode,
                            onClick = { onEvent(LangOnboardingUiEvent.OnLanguageSelected(it)) }
                        )
                    }
                }
                Spacer(Modifier.height(LangSpacing))
            }
        }
    )
}

// ================================================================================
// 6. FINAL PREVIEW
// ================================================================================

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
fun LangOnboardingScreenPreview() {
    // Mock Data (FIX: Now uses standard Material Icons)
    val mockLanguages = listOf(
        LangLanguageOption("es", "Spanish", Icons.Filled.Flag),
        LangLanguageOption("de", "German", Icons.Filled.Euro),
        LangLanguageOption("fr", "French", Icons.Filled.MenuBook),
        LangLanguageOption("ja", "Japanese", Icons.Filled.LocalFireDepartment),
        LangLanguageOption("pt", "Portuguese", Icons.Filled.Star)
    )

    val mockState = LangOnboardingUiState(
        availableLanguages = mockLanguages,
        selectedGoal = LangDailyGoal.ThirtyMin,
        selectedLanguageCode = "es",
        startLearningEnabled = true
    )

    LangOnboardingScreen(
        state = mockState,
        onEvent = {}
    )
}