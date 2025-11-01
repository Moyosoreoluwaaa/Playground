//package com.playground.movies.components
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.semantics.Role
//import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
//import com.playground.movies.uistates.MoviePosterItem
//
//// Assuming R.drawable.ic_image_placeholder and R.drawable.ic_error exist for a real app
//// Using standard Android resources for this minimal preview
//@Composable
//fun MovieCard(
//    movie: MoviePosterItem,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        shape = RoundedCornerShape(24.dp),
//        modifier = modifier
//            .aspectRatio(0.67f) // Common poster aspect ratio (2:3)
//            .clickable(
//                onClick = onClick,
//                role = Role.Image,
//                // Accessibility
//                onClickLabel = "View details for ${movie.title}"
//            ),
//        // Elevation can be dynamic, but we keep a constant small elevation for the look
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        AsyncImage(
//            model = movie.posterUrl,
//            contentDescription = "Movie poster for ${movie.title}",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize(),
//            // Placeholder and Error fallbacks
//            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
//            error = painterResource(id = android.R.drawable.ic_menu_delete)
//        )
//    }
//}