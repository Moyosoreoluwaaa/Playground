package com.playground.oakk.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.playground.oakk.OakkTheme
import com.playground.oakk.models.Product
import com.playground.oakk.models.SpecialAttribute

/**
 * A row item that displays information about a financial product.
 *
 * @param product The product data to display.
 * @param onClick The callback to be invoked when the item is clicked.
 * @param modifier The modifier to be applied to the component.
 */
@Composable
fun ProductRowItem(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "${product.name} card",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (product.name.contains("Green")) { // Example logic for badge
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.EnergySavingsLeaf,
                            contentDescription = "Green product",
                            tint = Color(0xFF3E8E41),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = product.balance,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (product.specialAttribute is SpecialAttribute.Points) {
                FilterChip(
                    selected = false,
                    onClick = { /* No-op, part of larger clickable row */ },
                    label = { Text("${product.specialAttribute.value} Points") },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        labelColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF5F5F3)
@Composable
private fun ProductRowItemPreview() {
    val sampleProduct = Product(
        id = "1",
        name = "Alinma You Card",
        imageUrl = "https://placehold.co/112x112/1D1D1B/F5F5F3?text=A",
        balance = "SAR 87,000.86"
    )
    OakkTheme {
        ProductRowItem(product = sampleProduct, onClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F3)
@Composable
private fun ProductRowItemWithPointsPreview() {
    val sampleProduct = Product(
        id = "2",
        name = "Alinma Green",
        imageUrl = "https://placehold.co/112x112/6B8E23/F5F5F3?text=A",
        balance = "$ 67,000.00",
        specialAttribute = SpecialAttribute.Points(10000)
    )
    OakkTheme {
        ProductRowItem(product = sampleProduct, onClick = {})
    }
}
