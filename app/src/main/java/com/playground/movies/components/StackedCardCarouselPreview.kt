//package com.playground.movies.components
//
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.playground.movies.uistates.MockUiState
//
//@Preview(showBackground = true, backgroundColor = 0xFF121212)
//@Composable
//fun StackedCardCarouselPreview() {
//    // Note: To fully appreciate the stacked effect, this preview needs to be interactive
//    // and run on a device/emulator, as the state calculations depend on real scroll position.
//    // The static preview shows the list of cards ready to be scrolled.
//    Row(Modifier.fillMaxWidth()) {
//        StackedCardCarousel(
//            uiState = MockUiState,
//            onMovieSelected = { println("Selected: ${it.title}") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(400.dp) // Define a fixed height for the component
//        )
//
//    }
//}