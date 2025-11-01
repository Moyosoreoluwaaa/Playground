package com.playground.crypto.presentation.tempo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.playground.ui.theme.PlaygroundTheme

// Domain model for preview
data class TempoProduct(
    val id: String,
    val name: String,
    val price: String,
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TempoProductCardClass(
    product: TempoProduct,
    onClick: (productId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick(product.id) },
        modifier = modifier
            .width(150.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = "Image of ${product.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Text(
                text = product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = product.price,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TempoProductCardClassPreview() {
    PlaygroundTheme {
        TempoProductCardClass(
            product = TempoProduct(
                id = "1",
                name = "T-shirt",
                price = "$120.82",
                imageUrl = "https://images.unsplash.com/photo-1579227181515-3b0f4f9b8702?ixlib=rb-4.0.3&q=80&fm=jpg&crop=entropy&cs=tinysrgb"
            ),
            onClick = {}
        )
    }
}
