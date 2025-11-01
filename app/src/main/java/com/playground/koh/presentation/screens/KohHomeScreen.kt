package com.playground.koh.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.playground.R
import com.playground.koh.domain.model.Category
import com.playground.koh.presentation.components.KohCategoryCard
import com.playground.koh.presentation.uistate.KohHomeEvent
import com.playground.koh.presentation.uistate.KohHomeUiState
import com.playground.ui.theme.PlaygroundTheme

@Composable
fun KohHomeScreen(
    uiState: KohHomeUiState,
    onEvent: (KohHomeEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            // "KOH" Logo
            Text(
                text = "KOH",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Light,
                fontSize = 150.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

//        Spacer(Modifier.height(8.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Category Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                uiState.categories.forEach { category ->
                    KohCategoryCard(
                        title = category.title,
                        iconUrl = category.iconUrl,
                        isFavorite = category.isFavorite,
                        onCardClick = { onEvent(KohHomeEvent.CategorySelected(category.id)) },
                        onFavoriteClick = {
                            onEvent(
                                KohHomeEvent.FavoriteToggled(
                                    category.id,
                                    !category.isFavorite
                                )
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // "New in" Section
            KohNewInSection(
                imageUrl = uiState.newInImageUrl,
                onNewInClick = { onEvent(KohHomeEvent.NewInClicked) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KohNewInSection(
    imageUrl: String,
    onNewInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onNewInClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.cream)
                    .crossfade(true)
                    .build(),
                contentDescription = "New in cosmetics hero image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = "New in",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}

// KohHomeScreen.kt

@Preview(showBackground = true)
@Composable
fun KohHomeScreenPreview() {
    PlaygroundTheme {
        KohHomeScreen(
            uiState = KohHomeUiState(
                isLoading = false,
                categories = listOf(
                    Category(
                        id = "1",
                        title = "Formula",
                        iconUrl = "https://example.com/formula_icon.png",
                        isFavorite = true
                    ),
                    Category(
                        id = "2",
                        title = "Bests",
                        iconUrl = "https://example.com/bests_icon.png"
                    )
                ),
                newInImageUrl = "https://example.com/new_in.jpg"
            ),
            onEvent = {}
        )
    }
}
