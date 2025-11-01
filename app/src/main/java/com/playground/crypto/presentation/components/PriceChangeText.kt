package com.playground.crypto.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PriceChangeText(change: Double, modifier: Modifier = Modifier) {
    val isPositive = change >= 0
    val color =
        if (isPositive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
    val icon = if (isPositive) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
    val sign = if (isPositive) "+" else ""
    val formattedChange = "%.2f".format(change)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = if (isPositive) "Price increased" else "Price decreased",
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "$sign$formattedChange%",
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PriceChangeTextPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        PriceChangeText(change = 0.96)
        PriceChangeText(change = -0.44)
    }
}
