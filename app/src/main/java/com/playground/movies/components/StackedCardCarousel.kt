//package com.playground.movies.components
//
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyListState
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.unit.dp
//import com.playground.movies.uistates.MovieCarouselUiState
//import com.playground.movies.uistates.MoviePosterItem
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun StackedCardCarousel(
//    uiState: MovieCarouselUiState,
//    onMovieSelected: (MoviePosterItem) -> Unit,
//    modifier: Modifier = Modifier,
//    lazyListState: LazyListState = rememberLazyListState()
//) {
//    val movies = uiState.featuredMovies
//    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
//    val focusedCardWidth = screenWidth * 0.75f // The width of the card when fully scaled
//    val contentPadding = screenWidth * 0.12f // Leaves a gap for the cards to stagger into
//
//    // Handle loading/error states
//    if (uiState.isLoading) {
//        // Show shimmer or loading indicator
//    }
//    if (uiState.errorMessage != null) {
//        // Show error message card
//    }
//    if (movies.isEmpty() && !uiState.isLoading) {
//        // Show empty state card
//    }
//
//    LazyRow(
//        state = lazyListState,
//        modifier = modifier.fillMaxWidth(),
//        contentPadding = PaddingValues(horizontal = contentPadding),
//        // Use 0.dp spacing here as the stacking is handled by translationX in the modifier
//        horizontalArrangement = Arrangement.spacedBy(0.dp)
//    ) {
//        itemsIndexed(movies, key = { _, movie -> movie.id }) { index, movie ->
//            MovieCard(
//                movie = movie,
//                onClick = { onMovieSelected(movie) },
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .width(focusedCardWidth)
//                    .stackedCarouselEffect(index, lazyListState, 5, focusedCardWidth)
//            )
//        }
//    }
//}