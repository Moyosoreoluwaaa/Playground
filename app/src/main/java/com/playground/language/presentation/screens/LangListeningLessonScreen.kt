package com.playground.language.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.language.domain.LangAnswer
import com.playground.language.presentation.LangPrimaryButton
import com.playground.language.presentation.components.LangAnswerChoice
import com.playground.language.presentation.components.LangAudioPlayer
import com.playground.language.presentation.components.LangSpacing
import com.playground.language.presentation.uistate.LangAudioPlayerState
import com.playground.language.presentation.uistate.LangListeningLessonUiEvent
import com.playground.language.presentation.uistate.LangListeningLessonUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LangListeningLessonScreen(
    state: LangListeningLessonUiState,
    onEvent: (LangListeningLessonUiEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.lessonTitle) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(LangListeningLessonUiEvent.OnBackClick) }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Go back to previous screen"
                        )
                    }
                },
            )
        },
        bottomBar = {
            Column(modifier = Modifier.padding(LangSpacing)) {
                LangPrimaryButton(
                    text = "Submit Answer",
                    onClick = { onEvent(LangListeningLessonUiEvent.OnSubmitAnswer) },
                    modifier = Modifier.fillMaxWidth(),
//                    enabled = state.submitButtonEnabled
                )
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
                // Audio Player Card
                LangAudioPlayer(
                    state = state.audioState,
                    onEvent = { event -> onEvent(LangListeningLessonUiEvent.OnAudioEvent(event)) },
                    modifier = Modifier.padding(top = LangSpacing)
                )

                Spacer(Modifier.height(LangSpacing * 2))

                // Question/Transcription Card
                Card(
                    shape = RoundedCornerShape(LangSpacing),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(LangSpacing * 1.5f)) {
                        Text(
                            text = state.transcriptionTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Divider(modifier = Modifier.padding(vertical = LangSpacing / 2))

                        Text(
                            text = state.lessonText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(LangSpacing))

                        // Answer Choices
                        state.choices.forEach { choice ->
                            LangAnswerChoice(
                                choice = choice,
                                isSelected = choice.id == state.selectedAnswerId,
                                onClick = { answer ->
                                    onEvent(LangListeningLessonUiEvent.OnAnswerSelected(answer))
                                },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(LangSpacing))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LangListeningLessonScreenPreview() {
    val mockChoices = listOf(
        LangAnswer("q1", "What Hunt the dog doing?", foreignText = null),
        LangAnswer("q2", "Der Hund spielt im Garten.", foreignText = null),
        LangAnswer(
            "q3",
            "Playing in the garden.",
            foreignText = "Der Hund spielt im Garten."
        ), // Selected in image
        LangAnswer("q4", "Sleeping", foreignText = null),
        LangAnswer("q5", "Eating", foreignText = null)
    )

    val mockState = LangListeningLessonUiState(
        audioState = LangAudioPlayerState(
            currentTime = 45_000,
            totalDuration = 150_000,
            isSegmentComplete = true,
            isPlaying = false
        ),
        choices = mockChoices,
        selectedAnswerId = "q3", // 'Playing in the garden' is selected
        submitButtonEnabled = true
    )

    LangListeningLessonScreen(
        state = mockState,
        onEvent = {}
    )
}