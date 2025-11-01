package com.playground.rooms.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.rooms.domain.model.OnboardingSlide
import com.playground.rooms.domain.model.OnboardingSlideContent
import com.playground.rooms.presentation.components.RoomsOnboardingSlide
import com.playground.rooms.presentation.uistate.OnboardingEvent
import com.playground.rooms.presentation.uistate.OnboardingUiState
import kotlinx.coroutines.launch

// --- SCREEN COMPOSABLE ---

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomsOnboardingScreen(
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit
) {
    val slides = uiState.slides
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0) { slides.size }

    // Sync ViewModel state with Pager state
    LaunchedEffect(pagerState.currentPage) {
        val newSlide = slides.getOrNull(pagerState.currentPage)?.slide ?: OnboardingSlide.CONNECT
        if (newSlide != uiState.currentPage) {
            onEvent(OnboardingEvent.PageSwiped(pagerState.currentPage))
        }
    }

    // Sync Pager state with ViewModel state
    LaunchedEffect(uiState.currentPage) {
        val targetIndex = OnboardingSlide.entries.indexOf(uiState.currentPage)
        if (pagerState.currentPage != targetIndex) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(targetIndex)
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val slideContent = slides[it]
            val isLastPage = it == slides.lastIndex

            RoomsOnboardingSlide(
                content = slideContent,
                isLastPage = isLastPage,
                onSkipClicked = { onEvent(OnboardingEvent.SkipClicked) },
                onNextClicked = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        // Note: The ViewModel/state transition handles updating uiState.currentPage
                        // which then feeds back into the LaunchedEffect above.
                    }
                },
                onProceedClicked = { onEvent(OnboardingEvent.ProceedClicked) }
            )
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, name = "Rooms Onboarding Flow Refined")
@Composable
fun RoomsOnboardingSlidesScreenPreview() {
    val sampleSlides = listOf(
        OnboardingSlideContent(
            slide = OnboardingSlide.CONNECT,
            headline = "Connect & Share",
            body = "Join rooms, meet new people, and share your ideas in friendly space.",
            illustrationResId = 0
        ),
        OnboardingSlideContent(
            slide = OnboardingSlide.DISCOVER,
            headline = "Discover & Engage",
            body = "Explore diverse communities and join conversations that match you interests.",
            illustrationResId = 1
        ),
        OnboardingSlideContent(
            slide = OnboardingSlide.SPACE_AWAITS,
            headline = "Your Space Awaits",
            body = "Step inside and find communities where you belong.",
            illustrationResId = 2
        )
    )

    MaterialTheme(colorScheme = lightColorScheme()) {
        RoomsOnboardingScreen(
            uiState = OnboardingUiState(
                slides = sampleSlides,
                currentPage = OnboardingSlide.CONNECT
            ),
            onEvent = {}
        )
    }
}
