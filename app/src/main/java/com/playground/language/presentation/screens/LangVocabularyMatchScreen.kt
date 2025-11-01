package com.playground.language.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.language.presentation.components.LangAppTheme
import com.playground.language.presentation.components.LangLessonMetricsRow
import com.playground.language.presentation.components.LangMatchNodeColumns
import com.playground.language.presentation.components.LangPageIndicator
import com.playground.language.presentation.components.LangPrimaryButton
import com.playground.language.presentation.components.LangSpacing
import com.playground.language.presentation.uistate.DragState
import com.playground.language.presentation.uistate.LangMatchPair
import com.playground.language.presentation.uistate.LangMatchWord
import com.playground.language.presentation.uistate.LangMatchWordType
import com.playground.language.presentation.uistate.LangVocabularyMatchPage
import com.playground.language.presentation.uistate.LangVocabularyMatchUiEvent
import com.playground.language.presentation.uistate.LangVocabularyMatchUiState
import kotlinx.coroutines.delay

@Composable
fun LangVocabularyMatchScreen(
    state: LangVocabularyMatchUiState,
    onEvent: (LangVocabularyMatchUiEvent) -> Unit
) {
    var currentState by remember { mutableStateOf(state) }
    val pagerState = rememberPagerState(
        initialPage = currentState.currentPage,
        pageCount = { currentState.pageCount })

    LaunchedEffect(state) {
        currentState = state
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != currentState.currentPage) {
            onEvent(LangVocabularyMatchUiEvent.OnPageChange(pagerState.currentPage))
        }
    }

    var nodeLayouts by remember { mutableStateOf(mapOf<String, LayoutCoordinates>()) }
    var dragState by remember { mutableStateOf<DragState?>(null) }
    var canvasLayout by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val ropeColor = MaterialTheme.colorScheme.primary
    val ropeStrokeWidth = with(LocalDensity.current) { 3.dp.toPx() }

    // Simulating timer and logic
    LaunchedEffect(key1 = Unit) {
        var seconds = 45
        while (seconds > 0) {
            delay(1000L)
            seconds--
            currentState = currentState.copy(
                liveTime = "0:${seconds.toString().padStart(2, '0')}"
            )
        }
    }

    val currentWords = currentState.currentPageData?.let {
        it.foreignWords + it.translationWords
    } ?: emptyList()

    val handleDragEnd: () -> Unit = {
        val currentDragState = dragState
        if (currentDragState != null) {
            val targetNodeEntry = nodeLayouts.entries.find { (id, layout) ->
                layout.isAttached &&
                        layout.boundsInRoot().contains(currentDragState.currentOffset) &&
                        id != currentDragState.startWord.id
            }

            if (targetNodeEntry != null) {
                val targetWord = currentWords.find { it.id == targetNodeEntry.key }
                val startWord = currentDragState.startWord

                if (targetWord != null && startWord.type != targetWord.type) {
                    val filteredMatches = currentState.currentMatches.filter {
                        it.wordIdA != startWord.id && it.wordIdB != startWord.id &&
                                it.wordIdA != targetWord.id && it.wordIdB != targetWord.id
                    }
                    val newMatch = if (startWord.type == LangMatchWordType.FOREIGN) {
                        LangMatchPair(startWord.id, targetWord.id)
                    } else {
                        LangMatchPair(targetWord.id, startWord.id)
                    }
                    val newMatches = (filteredMatches + newMatch).distinct()

                    currentState = currentState.copy(
                        currentMatches = newMatches,
                        checkMatchesEnabled = newMatches.size == currentState.currentPageData?.foreignWords?.size,
                        livePoints = currentState.livePoints + 15,
                        liveStreak = if (newMatches.size % 3 == 0) currentState.liveStreak + 1 else currentState.liveStreak
                    )

                    onEvent(LangVocabularyMatchUiEvent.OnMatchMade(newMatches))
                }
            }
        }
        dragState = null
    }

    val calculateControlPoints: (Offset, Offset) -> Pair<Offset, Offset> = { start, end ->
        val midX = (start.x + end.x) / 2f
        val controlPoint1 = Offset(x = midX, y = start.y)
        val controlPoint2 = Offset(x = midX, y = end.y)
        Pair(controlPoint1, controlPoint2)
    }

    val pageIndex = 2

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(LangSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onEvent(LangVocabularyMatchUiEvent.OnBackClick) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back"
                            )
                        }

                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = currentState.lessonTitle,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    LangPageIndicator(
                        currentPage = pageIndex,
                        pageCount = currentState.pageCount
                    )

                    Spacer(Modifier.height(26.dp))

                    LangLessonMetricsRow(
                        streak = currentState.liveStreak,
                        points = currentState.livePoints,
                        time = currentState.liveTime,
                        modifier = Modifier.padding(bottom = LangSpacing)
                    )
                }

            }
        },
        bottomBar = {
            Column(modifier = Modifier.padding(LangSpacing)) {
                LangPrimaryButton(
                    text = if (currentState.currentPage < currentState.pageCount - 1) "Next Page" else "Check Matches",
                    onClick = {
                        if (currentState.currentPage < currentState.pageCount - 1) {
                            onEvent(LangVocabularyMatchUiEvent.OnPageChange(currentState.currentPage + 1))
                        } else {
                            onEvent(LangVocabularyMatchUiEvent.OnCheckMatchesClick)
                        }
                    },
                    enabled = currentState.checkMatchesEnabled
                )
            }
        },
        content = { paddingValues ->
            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) { pageIndex ->
                val pageData = currentState.pages.getOrNull(pageIndex)
                if (pageData != null) {
                    Column(
                        modifier = Modifier
//                            .fillMaxSize()
                            .padding(horizontal = LangSpacing),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) { // 🌟 ADDED: Progress Indicator and Page Text


                        Spacer(Modifier.height(20.dp))

                        Spacer(Modifier.height(LangSpacing))

                        Card(
                            shape = RoundedCornerShape(LangSpacing),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
//                                .weight(1f)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
//                                        .weight(1f)
                                        .onGloballyPositioned { canvasLayout = it }
                                ) {
                                    Canvas(modifier = Modifier.matchParentSize()) {
                                        val currentCanvasLayout = canvasLayout ?: return@Canvas
                                        val canvasTopLeft =
                                            currentCanvasLayout.boundsInRoot().topLeft
                                        val path = Path()

                                        currentState.currentMatches.forEach { match ->
                                            val startLayout = nodeLayouts[match.wordIdA]
                                            val endLayout = nodeLayouts[match.wordIdB]
                                            if (startLayout != null && endLayout != null) {
                                                val start =
                                                    startLayout.boundsInRoot().center - canvasTopLeft
                                                val end =
                                                    endLayout.boundsInRoot().center - canvasTopLeft
                                                val (cp1, cp2) = calculateControlPoints(start, end)
                                                path.reset()
                                                path.moveTo(start.x, start.y)
                                                path.cubicTo(
                                                    cp1.x,
                                                    cp1.y,
                                                    cp2.x,
                                                    cp2.y,
                                                    end.x,
                                                    end.y
                                                )
                                                drawPath(
                                                    path,
                                                    ropeColor,
                                                    style = Stroke(
                                                        ropeStrokeWidth,
                                                        cap = StrokeCap.Round
                                                    )
                                                )
                                            }
                                        }

                                        dragState?.let {
                                            val start = it.startOffset - canvasTopLeft
                                            val end = it.currentOffset - canvasTopLeft
                                            val (cp1, cp2) = calculateControlPoints(start, end)
                                            path.reset()
                                            path.moveTo(start.x, start.y)
                                            path.cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, end.x, end.y)
                                            drawPath(
                                                path,
                                                ropeColor,
                                                style = Stroke(
                                                    ropeStrokeWidth,
                                                    cap = StrokeCap.Round
                                                )
                                            )
                                        }
                                    }

                                    LangMatchNodeColumns(
                                        words = pageData.foreignWords to pageData.translationWords,
                                        currentMatches = currentState.currentMatches,
                                        onNodePositioned = { word, layout ->
                                            nodeLayouts = nodeLayouts + (word.id to layout)
                                        },
                                        onDragStart = { word ->
                                            val isMatched =
                                                currentState.currentMatches.any { it.wordIdA == word.id || it.wordIdB == word.id }
                                            if (!isMatched) {
                                                nodeLayouts[word.id]?.boundsInRoot()?.center?.let {
                                                    dragState = DragState(word, it, it)
                                                }
                                            }
                                        },
                                        onDrag = { dragAmount ->
                                            dragState?.let {
                                                dragState =
                                                    it.copy(currentOffset = it.currentOffset + dragAmount)
                                            }
                                        },
                                        onDragEnd = handleDragEnd
                                    )
                                }
                            }
                            Spacer(Modifier.height(LangSpacing))
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true, widthDp = 380)
@Composable
fun LangVocabularyMatchScreenFinalPreview() {
    val page1Foreign = listOf(
        LangMatchWord("Hund", "Hund", LangMatchWordType.FOREIGN),
        LangMatchWord("Katze", "Katze", LangMatchWordType.FOREIGN),
        LangMatchWord("Essen", "Essen", LangMatchWordType.FOREIGN),
        LangMatchWord("Trinken", "Trinken", LangMatchWordType.FOREIGN),
        LangMatchWord("Garten", "Garten", LangMatchWordType.FOREIGN)
    )

    val page1Translation = listOf(
        LangMatchWord("Cat", "Katze", LangMatchWordType.TRANSLATION),
        LangMatchWord("Garden", "Garten", LangMatchWordType.TRANSLATION),
        LangMatchWord("Food", "Essen", LangMatchWordType.TRANSLATION),
        LangMatchWord("Drink", "Trinken", LangMatchWordType.TRANSLATION),
        LangMatchWord("Dog", "Hund", LangMatchWordType.TRANSLATION)
    ).shuffled()

    val page1CorrectMatches = listOf(
        LangMatchPair("Hund", "Dog"),
        LangMatchPair("Katze", "Cat"),
        LangMatchPair("Essen", "Food"),
        LangMatchPair("Trinken", "Drink"),
        LangMatchPair("Garten", "Garden")
    )

    val page2Foreign = listOf(
        LangMatchWord("Apfel", "Apfel", LangMatchWordType.FOREIGN),
        LangMatchWord("Buch", "Buch", LangMatchWordType.FOREIGN),
        LangMatchWord("Wasser", "Wasser", LangMatchWordType.FOREIGN),
        LangMatchWord("Sonne", "Sonne", LangMatchWordType.FOREIGN),
        LangMatchWord("Haus", "Haus", LangMatchWordType.FOREIGN)
    )

    val page2Translation = listOf(
        LangMatchWord("Sun", "Sonne", LangMatchWordType.TRANSLATION),
        LangMatchWord("Water", "Wasser", LangMatchWordType.TRANSLATION),
        LangMatchWord("Book", "Buch", LangMatchWordType.TRANSLATION),
        LangMatchWord("House", "Haus", LangMatchWordType.TRANSLATION),
        LangMatchWord("Apple", "Apfel", LangMatchWordType.TRANSLATION)
    ).shuffled()

    val page2CorrectMatches = listOf(
        LangMatchPair("Apfel", "Apple"),
        LangMatchPair("Buch", "Book"),
        LangMatchPair("Wasser", "Water"),
        LangMatchPair("Sonne", "Sun"),
        LangMatchPair("Haus", "House")
    )

    val pages = listOf(
        LangVocabularyMatchPage(page1Foreign, page1Translation, page1CorrectMatches),
        LangVocabularyMatchPage(page2Foreign, page2Translation, page2CorrectMatches)
    )

    val mockState = LangVocabularyMatchUiState(
        pages = pages,
        pageCount = pages.size,
        currentPage = 0,
        currentMatches = emptyList(),
        checkMatchesEnabled = false
    )

    var previewState by remember { mutableStateOf(mockState) }

    LangAppTheme {
        LangVocabularyMatchScreen(
            state = previewState,
            onEvent = { event ->
                when (event) {
                    is LangVocabularyMatchUiEvent.OnBackClick -> { /* Do nothing for preview */
                    }

                    is LangVocabularyMatchUiEvent.OnCheckMatchesClick -> { /* Do nothing for preview */
                    }

                    is LangVocabularyMatchUiEvent.OnPageChange -> {
                        previewState = previewState.copy(
                            currentPage = event.newPageIndex,
                            currentMatches = emptyList(),
                            checkMatchesEnabled = false
                        )
                    }

                    is LangVocabularyMatchUiEvent.OnMatchMade -> {
                        previewState = previewState.copy(
                            currentMatches = event.newMatches,
                            checkMatchesEnabled = event.newMatches.size == previewState.currentPageData?.foreignWords?.size,
                            livePoints = previewState.livePoints + 15,
                            liveStreak = if (event.newMatches.size % 3 == 0) previewState.liveStreak + 1 else previewState.liveStreak
                        )
                    }
                }
            }
        )
    }
}
