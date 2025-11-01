package com.playground.layer.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.playground.layer.presentation.theme.CardOnBackground

@Composable
fun RecipientAddressInput(
    address: String,
    onAddressChange: (String) -> Unit,
    onScanQr: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = address,
        onValueChange = onAddressChange,
        label = { Text("Recipient Address") },
        trailingIcon = {
            IconButton(onClick = onScanQr) {
                Icon(
                    Icons.Default.QrCodeScanner,
                    contentDescription = "Scan Address"
                )
            }
        },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = CardOnBackground
        )
    )
}