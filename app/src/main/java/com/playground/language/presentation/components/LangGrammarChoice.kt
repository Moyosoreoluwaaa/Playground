package com.playground.language.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.language.domain.LangGrammarOption
import com.playground.language.presentation.LangPrimaryButton
import com.playground.language.presentation.uistate.LangGrammarExerciseUiEvent
import com.playground.language.presentation.uistate.LangGrammarExerciseUiState


// Reusing LangSpacing (16.dp) and LangPrimaryButton from prior steps

@Composable
fun LangGrammarChoice(
    choice: LangGrammarOption,
    isSelected: Boolean,
    isCorrect: Boolean, // To handle post-check state
    isAnswerChecked: Boolean,
    onClick: (LangGrammarOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val successColor = Color(0xFF4CAF50) // Green
    val errorColor = Color(0xFFF44336) // Red

    val targetColor = when {
        !isAnswerChecked && isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) // Before check, selected
        isAnswerChecked && isCorrect -> successColor // After check, correct
        isAnswerChecked && isSelected -> errorColor // After check, incorrect
        else -> MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp) // Default unselected
    }

    val textColor =
        if (!isAnswerChecked && isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Card(
        shape = RoundedCornerShape(LangSpacing / 2),
        colors = CardDefaults.cardColors(containerColor = targetColor),
        elevation = CardDefaults.cardElevation(if (isSelected) 2.dp else 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .selectable(
                selected = isSelected,
                onClick = { if (!isAnswerChecked) onClick(choice) },
                role = androidx.compose.ui.semantics.Role.RadioButton
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LangSpacing / 2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = choice.text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${choice.completionPercentage}%",
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.6f),
                textAlign = TextAlign.End
            )
        }
    }
}


@Composable
fun LangGrammarExerciseScreen(
    state: LangGrammarExerciseUiState,
    onEvent: (LangGrammarExerciseUiEvent) -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(horizontal = LangSpacing, vertical = LangSpacing)) {
                Text(
                    text = state.lessonTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        bottomBar = {
            Column(modifier = Modifier.padding(LangSpacing)) {
                LangPrimaryButton(
                    text = if (state.isAnswerChecked) "Continue" else "Check Answer",
                    onClick = {
                        if (state.isAnswerChecked) {
                            // OnContinueClick event (not formally defined, but implied by flow)
                        } else {
                            onEvent(LangGrammarExerciseUiEvent.OnCheckAnswer)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
//                    enabled = state.checkButtonEnabled
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
                HorizontalDivider(
                    modifier = Modifier
//                        .width(120.dp)
                        .padding(top = 4.dp, bottom = LangSpacing),
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )

                Spacer(Modifier.height(LangSpacing))
                // Question Prompt
                Text(
                    text = state.questionSentence,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = state.instruction,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = LangSpacing * 2)
                )

                // Answer Grid (Paired Layout)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(LangSpacing / 2)
                ) {
                    val groupedOptions = state.options.chunked(2)
                    groupedOptions.forEach { rowOptions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(LangSpacing)
                        ) {
                            rowOptions.forEach { option ->
                                LangGrammarChoice(
                                    choice = option,
                                    isSelected = option.id == state.selectedOptionId,
                                    isCorrect = option.isCorrect,
                                    isAnswerChecked = state.isAnswerChecked,
                                    onClick = { selected ->
                                        onEvent(LangGrammarExerciseUiEvent.OnOptionSelected(selected))
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Add an empty space if the last row has only one item
                            if (rowOptions.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LangGrammarExerciseScreenPreview() {
    val mockOptions = listOf(
        LangGrammarOption("o1", "schläft", isCorrect = true),
        LangGrammarOption("o2", "schlafen"),
        LangGrammarOption("o3", "schlafen"),
        LangGrammarOption("o4", "schlafe"),
        LangGrammarOption("o5", "schläft"), // Duplicate for layout testing
        LangGrammarOption("o6", "schlafe")
    )

    val mockState = LangGrammarExerciseUiState(
        options = mockOptions,
        selectedOptionId = "o1", // 'schläft' is selected
        checkButtonEnabled = true,
        isAnswerChecked = false // Pre-submission state
    )

    LangGrammarExerciseScreen(
        state = mockState,
        onEvent = { event ->
            if (event is LangGrammarExerciseUiEvent.OnCheckAnswer) {
                // Simulating the state change for post-check preview
                // This logic would normally be handled by the ViewModel
            }
        }
    )
}