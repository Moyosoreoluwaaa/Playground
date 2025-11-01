//package com.playground.movies.uistates
//
//import androidx.compose.runtime.Immutable
//
//@Immutable
//data class MoviePosterItem(
//    val id: String,
//    val title: String, // Used for content description
//    val posterUrl: String
//)
//
//data class MovieCarouselUiState(
//    val featuredMovies: List<MoviePosterItem> = emptyList(),
//    val isLoading: Boolean = false,
//    val errorMessage: String? = null
//)
//
//val MockMovies = listOf(
//    MoviePosterItem("1", "TRON: ARES", "https://i.imgur.com/G9Cj1wN.png"), // Dummy URL
//    MoviePosterItem("2", "The Creator", "https://i.imgur.com/8Qj2xXw.png"), // Dummy URL
//    MoviePosterItem("3", "Dune: Part Two", "https://i.imgur.com/7XqY9wT.png"), // Dummy URL
//    MoviePosterItem("4", "Blade Runner 2049", "https://i.imgur.com/h5TzY7H.png"), // Dummy URL
//    MoviePosterItem("5", "Mad Max: Fury Road", "https://i.imgur.com/h5TzY7H.png"), // Dummy URL
//    MoviePosterItem("6", "Arrival", "https://i.imgur.com/h5TzY7H.png"), // Dummy URL
//)
//val MockUiState = MovieCarouselUiState(featuredMovies = MockMovies)
