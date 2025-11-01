package com.playground.koh.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.playground.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KohCategoryCard(
    title: String,
    iconUrl: String,
    isFavorite: Boolean,
    onCardClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onCardClick,
        modifier = modifier
            .width(180.dp)
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Main icon for the card
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.cream)
                        .crossfade(true)
                        .build(),
                    contentDescription = "$title category icon",
                    modifier = Modifier.size(64.dp)
                )

                // Favorite button, only for "Formula" card as per the UI
                if (title == "Formula") {
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            painter = if (isFavorite) {
                                painterResource(id = R.drawable.ic_launcher_background)
                            } else {
                                painterResource(id = R.drawable.ic_launcher_background)
                            },
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Title of the card
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// KohCategoryCard.kt


@Preview(name = "Formula Card - Unselected")
@Composable
fun KohFormulaCardPreviewUnselected() {
    MaterialTheme {
        KohCategoryCard(
            title = "Formula",
            iconUrl = "https://example.com/formula_icon.png", // Mock URL
            isFavorite = false,
            onCardClick = {},
            onFavoriteClick = {}
        )
    }
}

@Preview(name = "Formula Card - Selected")
@Composable
fun KohFormulaCardPreviewSelected() {
    MaterialTheme {
        KohCategoryCard(
            title = "Formula",
            iconUrl = "https://example.com/formula_icon.png", // Mock URL
            isFavorite = true,
            onCardClick = {},
            onFavoriteClick = {}
        )
    }
}

@Preview(name = "Bests Card")
@Composable
fun KohBestsCardPreview() {
    MaterialTheme {
        KohCategoryCard(
            title = "Bests",
            iconUrl = "https://example.com/bests_icon.png", // Mock URL
            isFavorite = false,
            onCardClick = {},
            onFavoriteClick = {}
        )
    }
}
