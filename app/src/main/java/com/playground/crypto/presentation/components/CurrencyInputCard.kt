package com.playground.crypto.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.crypto.domain.model.Currency

@Composable
fun CurrencyInputCard(
    cardTitle: String,
    selectedCurrency: Currency,
    amount: String,
    balance: String,
    onAmountChange: (String) -> Unit,
    onCurrencySelectClick: () -> Unit,
    onMaxClick: () -> Unit,
    modifier: Modifier = Modifier,
    isInputEnabled: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
                MaterialTheme.shapes.large
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = cardTitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onCurrencySelectClick) {
                CoinIcon(
                    iconUrl = selectedCurrency.iconUrl,
                    contentDescription = "${selectedCurrency.name} logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedCurrency.symbol.uppercase(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select currency"
                )
            }
        }
        BasicTextField(
            value = amount,
            onValueChange = onAmountChange,
            enabled = isInputEnabled,
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                color = if (isInputEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (amount.isEmpty() && isInputEnabled) {
                        Text(
                            text = "0",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Balance: $balance",
                style = MaterialTheme.typography.labelMedium
            )
            if (isInputEnabled) {
                TextButton(onClick = onMaxClick, modifier = Modifier.height(32.dp)) {
                    Text("Max")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CurrencyInputCardPreview() {
    val btc = Currency("BTC", "Bitcoin", "")
    val eth = Currency("ETH", "Ethereum", "")
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CurrencyInputCard(
            cardTitle = "Swap from",
            selectedCurrency = btc,
            amount = "0.1705",
            balance = "1.28819212",
            onAmountChange = {},
            onCurrencySelectClick = {},
            onMaxClick = {},
            isInputEnabled = true
        )
        CurrencyInputCard(
            cardTitle = "To",
            selectedCurrency = eth,
            amount = "4.21",
            balance = "10.5",
            onAmountChange = {},
            onCurrencySelectClick = {},
            onMaxClick = {},
            isInputEnabled = false
        )
    }
}
