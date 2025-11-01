package com.playground.crypto.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.crypto.domain.model.Coin

@Composable
fun CoinListItem(
    coin: Coin,
    onCoinClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onCoinClick(coin.id) }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            coin.iconUrl?.let { CoinIcon(iconUrl = it, contentDescription = "${coin.name} logo") }
            Column {
                Text(
                    text = coin.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = coin.symbol.uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$%,.2f".format(coin.currentPrice),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            PriceChangeText(change = coin.priceChange24h)
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CoinListItemPreview() {
    val btc = Coin("bitcoin", "BTC", "Bitcoin", "", 98257.81, -0.44)
    val bnb = Coin("binancecoin", "BNB", "Binance", "", 703.57, 0.96)
    Column(modifier = Modifier.padding(16.dp)) {
        CoinListItem(coin = btc, onCoinClick = {})
        CoinListItem(coin = bnb, onCoinClick = {})
    }
}
