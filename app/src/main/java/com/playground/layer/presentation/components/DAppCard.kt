package com.playground.layer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.playground.layer.data.source.GENERIC_DAPP_ICON_URL
import com.playground.layer.domain.model.DApp

@Composable
fun DAppCard(dapp: DApp, onClick: (DApp) -> Unit, modifier: Modifier = Modifier) {
    val cardSize = 120.dp // Slightly larger to accommodate text better on image

    // Logic check: Uses iconUrl if available, otherwise falls back to generic
    val imageUrl = dapp.iconUrl.ifEmpty { GENERIC_DAPP_ICON_URL }

    Card(
        shape = MaterialTheme.shapes.medium,
//        onClick = onClick,
        modifier = modifier.size(cardSize) // Apply size to the Card itself
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Image as background
            AsyncImage(
                model = imageUrl,
                contentDescription = "${dapp.name} background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Content within the Box (Name, Category, Badge)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp), // Padding inside the card
                verticalArrangement = Arrangement.Bottom, // Align content to the bottom
                horizontalAlignment = Alignment.Start
            ) {
                // DApp Name
                Text(
                    text = dapp.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White // Text color for readability on image
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // DApp Category
                Text(
                    text = dapp.category,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.LightGray // Text color for readability on image
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Badge/Tag - Positioned independently at TopEnd
            dapp.badgeText?.let {
                Text(
                    text = it,
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Align badge to top end of the Box
                        .padding(4.dp) // Small padding from card edges
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}