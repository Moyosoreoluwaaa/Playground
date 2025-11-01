package com.playground.language.presentation.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import com.playground.language.presentation.uistate.LangMatchPair
import com.playground.language.presentation.uistate.LangMatchWord

@Composable
fun LangMatchNodeColumns(
    words: Pair<List<LangMatchWord>, List<LangMatchWord>>,
    currentMatches: List<LangMatchPair>,
    onNodePositioned: (word: LangMatchWord, layout: LayoutCoordinates) -> Unit,
    onDragStart: (word: LangMatchWord) -> Unit,
    onDrag: (dragAmount: Offset) -> Unit,
    onDragEnd: () -> Unit,
) {
    val (foreignWords, translationWords) = words

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LangSpacing),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
//            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(LangSpacing),
            horizontalAlignment = Alignment.Start
        ) {
            foreignWords.forEach { word ->
                val isMatched =
                    currentMatches.any { it.wordIdA == word.id || it.wordIdB == word.id }
                LangDraggableMatchNodeLeft(
                    word = word, isMatched = isMatched,
                    modifier = Modifier
                        .onGloballyPositioned { onNodePositioned(word, it) }
                        .pointerInput(word.id) {
                            detectDragGestures(
                                onDragStart = { onDragStart(word) },
                                onDragEnd = onDragEnd,
                                onDragCancel = onDragEnd,
                                onDrag = { _, dragAmount -> onDrag(dragAmount) }
                            )
                        }
                )
            }
        }
        Spacer(Modifier.width(LangSpacing))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(LangSpacing),
            horizontalAlignment = Alignment.End
        ) {
            translationWords.forEach { word ->
                val isMatched =
                    currentMatches.any { it.wordIdA == word.id || it.wordIdB == word.id }
                LangDraggableMatchNodeRight(
                    word = word, isMatched = isMatched,
                    modifier = Modifier
                        .onGloballyPositioned { onNodePositioned(word, it) }
                        .pointerInput(word.id) {
                            detectDragGestures(
                                onDragStart = { onDragStart(word) },
                                onDragEnd = onDragEnd,
                                onDragCancel = onDragEnd,
                                onDrag = { _, dragAmount -> onDrag(dragAmount) }
                            )
                        }
                )
            }
        }
    }
}