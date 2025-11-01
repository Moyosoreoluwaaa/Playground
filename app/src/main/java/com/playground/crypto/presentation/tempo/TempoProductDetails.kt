package com.playground.crypto.presentation.tempo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playground.ui.theme.PlaygroundTheme

// Domain models
data class TempoProductDetails(
    val id: String,
    val name: String,
    val price: String,
    val description: String,
    val imageUrls: List<String>,
    val isFavorited: Boolean
)

@Composable
fun TempoProductDetailsScreenClass(
    productDetails: TempoProductDetails,
    onBackClicked: () -> Unit,
    onFavoriteClicked: (String) -> Unit,
    onShareClicked: () -> Unit,
    onAddToCartClicked: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TempoTopBarClass(
            title = "",
            leadingIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            trailingIcons = {
                Row {
                    IconButton(onClick = { onFavoriteClicked(productDetails.id) }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = if (productDetails.isFavorited) Color.Red else Color.Gray
                        )
                    }
                    IconButton(onClick = onShareClicked) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
                    }
                }
            }
        )

        TempoProductImagePagerClass(
            imageUrls = productDetails.imageUrls,
            modifier = Modifier.weight(1f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = productDetails.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = productDetails.price,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = productDetails.description,
                fontSize = 14.sp
            )
            
            Button(
                onClick = { onAddToCartClicked(productDetails.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Add to Cart",
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TempoProductDetailsScreenClassPreview() {
    PlaygroundTheme {
        TempoProductDetailsScreenClass(
            productDetails = TempoProductDetails(
                id = "1",
                name = "Sage green T-shirt",
                price = "$120.82",
                description = "Crafted from soft organic cotton, this t-shirt features a subtle tone-on-tone embroidery, ensuring both comfort and style. Perfect for casual wear.",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1571439284488-82163b45155c?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                    "https://images.unsplash.com/photo-1571439284488-82163b45155c?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                ),
                isFavorited = true
            ),
            onBackClicked = {},
            onFavoriteClicked = {},
            onShareClicked = {},
            onAddToCartClicked = {}
        )
    }
}
