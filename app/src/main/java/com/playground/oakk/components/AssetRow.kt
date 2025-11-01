package com.playground.oakk.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.playground.oakk.models.Asset

/**
 * A row item that displays information about a single investment asset.
 *
 * @param asset The asset data to display.
 * @param onClick The callback to be invoked when the item is clicked.
 * @param modifier The modifier to be applied to the component.
 */

@Composable
fun AssetRow(
    asset: Asset,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onClick(asset.id) },
        modifier = modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(asset.logoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "${asset.name} logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(
                        text = asset.ticker,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = asset.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text = asset.valueInSar,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            PerformanceChip(percentageChange = asset.percentageChange)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F3)
@Composable
private fun AssetRowStockPreview() {
    val stock = Asset(
        id = "1",
        ticker = "AMZN",
        name = "Amazon",
        description = "(86 shares)",
        logoUrl = "https://placehold.co/96x96/FF9900/000000?text=A",
        valueInSar = "SAR 32,000.75",
        percentageChange = 1.42
    )
    OakkTheme {
        AssetRow(asset = stock, onClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F3)
@Composable
private fun AssetRowAccountPreview() {
    val account = Asset(
        id = "4",
        ticker = "Bank of America",
        name = "Bank of America",
        description = "",
        logoUrl = "https://placehold.co/96x96/E31837/FFFFFF?text=B",
        valueInSar = "SAR 200,000.00",
        percentageChange = 16.00
    )
    OakkTheme {
        AssetRow(asset = account, onClick = {})
    }
}
