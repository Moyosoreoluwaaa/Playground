package com.playground.language.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.playground.language.presentation.LangPrimaryButton
import com.playground.language.presentation.components.LangAppTheme
import com.playground.language.presentation.components.LangInterestCloud
import com.playground.language.presentation.components.LangSpacing
import com.playground.language.presentation.uistate.LangInterest
import com.playground.language.presentation.uistate.LangInterestSelectionUiEvent
import com.playground.language.presentation.uistate.LangInterestSelectionUiState

@Composable
fun LangInterestSelectionScreen(
    state: LangInterestSelectionUiState,
    onEvent: (LangInterestSelectionUiEvent) -> Unit
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
                IconButton(onClick = { onEvent(LangInterestSelectionUiEvent.OnBackClick) }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                TextButton(onClick = { onEvent(LangInterestSelectionUiEvent.OnSkipClick) }) {
                    Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        bottomBar = {
            Column(modifier = Modifier.padding(LangSpacing)) {
                LangPrimaryButton(
                    text = "Start Learning!",
                    onClick = { onEvent(LangInterestSelectionUiEvent.OnStartLearningClick) },
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
                    .padding(horizontal = LangSpacing * 1.5f),
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

                // 2. Interest Cloud
                LangInterestCloud(
                    interests = state.availableInterests,
                    onInterestToggled = { onEvent(LangInterestSelectionUiEvent.OnInterestToggled(it)) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = LangSpacing * 2)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LangInterestSelectionScreenPreview() {
    val topics = listOf(
        "Travel", "Food", "Art", "Travel",
        "Technology", "History",
        "Nature", "Daily life", "Music", "Family",
        "Movies", "Sports", "Science", "Work",
        "Politics", "Health", "Cooking", "Space",
        "Slang", "News", "Film", "Gaming", "Literature"
    )

    val mockInterests = topics.mapIndexed { index, name ->
        LangInterest(
            id = index.toString(),
            name = name,
            isSelected = name == "Music" || name == "Movies"
        )
    }

    val mockState = LangInterestSelectionUiState(
        availableInterests = mockInterests,
        startLearningEnabled = true
    )

    LangAppTheme {
        LangInterestSelectionScreen(
            state = mockState,
            onEvent = {}
        )
    }
}