package com.playground.movies

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlin.math.absoluteValue

// region --------- Domain Model & UiState (Unchanged) ---------

data class Movie(
    val id: String,
    val title: String,
    val releaseYear: String,
    val posterUrl: String,
)

data class MovieCarouselUiState(
    val movies: List<Movie>,
)

// endregion

// region --------- Reusable Components (Unchanged) ---------

@Composable
fun MovieInfo(movie: Movie, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = movie.releaseYear,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun MoviePosterCard(movie: Movie, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(20.dp), modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.posterUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Poster for ${movie.title}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
        )
    }
}

// endregion

// region --------- Screen Composable (Updated) ---------

@Composable
fun MovieCarouselScreen(
    uiState: MovieCarouselUiState,
    onMovieClicked: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.movies.isEmpty()) {
        return
    }

    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    val activeMovieIndex by remember {
        derivedStateOf { pagerState.currentPage % uiState.movies.size }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            modifier = Modifier.height(350.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val movieIndex = page % uiState.movies.size
                val movie = uiState.movies[movieIndex]
                val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

                // **CHANGE**: Increased the scaling effect slightly to maintain depth
                val scale = 1f - (pageOffset.absoluteValue * 0.33f)
                val rotationY = pageOffset * -35f
                // **CHANGE**: Increased the divisor to bring cards closer horizontally
                val translationX = pageOffset * (maxWidth.value / 4.8f)
                val translationY = pageOffset.absoluteValue * -50f

                MoviePosterCard(
                    movie = movie,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .aspectRatio(2f / 3f)
                        .graphicsLayer {
                            this.scaleX = scale
                            this.scaleY = scale
                            this.rotationY = rotationY
                            this.translationX = translationX
                            this.translationY = translationY
                        }
                        .zIndex(100f - pageOffset.absoluteValue)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(
            targetState = uiState.movies[activeMovieIndex],
            label = "movieInfoAnimation",
            transitionSpec = {
                (slideInVertically { h -> h } + fadeIn())
                    .togetherWith(slideOutVertically { h -> -h } + fadeOut())
            }
        ) { movie ->
            MovieInfo(movie)
        }
    }
}

// endregion

// region --------- Preview (Unchanged) ---------

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun MovieCarouselScreenPreview() {
    val sampleMovies = remember {
        listOf(
            Movie("0", "Oppenheimer", "2023", "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg"),
            Movie("1", "Dune: Part Two", "2024", "https://image.tmdb.org/t/p/w500/8b8R8l88Qje9dn9OE8So054Z0eN.jpg"),
            Movie("2", "Tron: Ares", "2025", "https://www.themoviedb.org/t/p/w500/vTLPnK3rDefM3a1i0a8HchjMmg.jpg"),
            Movie("3", "Blade Runner 2049", "2017", "https://image.tmdb.org/t/p/w500/gajva2L0rPYkEWjzgFlBXCAVBE5.jpg"),
            Movie("4", "The Creator", "2023", "https://image.tmdb.org/t/p/w500/vB8o2p4ETnrfiWEgVxHmHWP9yRl.jpg")
        )
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MovieCarouselScreen(
                uiState = MovieCarouselUiState(movies = sampleMovies),
                onMovieClicked = { }
            )
        }
    }
}
// endregion