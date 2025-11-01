package com.playground.layer.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.playground.layer.domain.model.Asset
import com.playground.layer.domain.model.SwapType
import com.playground.layer.presentation.theme.CardOnBackground

@Composable
fun CurrencyField(
    type: SwapType,
    asset: Asset?,
    amount: String,
    usdValue: String?,
    isInputMode: Boolean, // True when amount is being input/displayed (right side of image)
    showQrScanner: Boolean = false,
    onAssetClick: () -> Unit,
    onAmountChange: (String) -> Unit = {},
    onScanQr: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp)
            .border(
                width = if (isInputMode && type == SwapType.SEND) 2.dp else 0.dp,
                color = if (isInputMode && type == SwapType.SEND) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.extraLarge
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Asset Icon and Ticker (Clickable to change asset)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onAssetClick),
                horizontalAlignment = Alignment.Start
            ) {
                if (asset != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = asset.iconUrl,
                            contentDescription = "${asset.name} icon",
                            placeholder = ColorPainter(Color.LightGray),
                            error = ColorPainter(Color.Red),
                            modifier = Modifier.size(28.dp).clip(CircleShape)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = asset.name,
                            style = MaterialTheme.typography.titleLarge.copy(color = CardOnBackground),
                            maxLines = 1
                        )
                    }
                    if (!isInputMode || type == SwapType.RECEIVE) {
                        // Display Ticker below name in selection mode, or USD value in receive mode
                        Text(
                            text = if (!isInputMode) asset.ticker else usdValue.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                            maxLines = 1
                        )
                    }
                }
            }

            // 2. Amount Input/Display
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                if (type == SwapType.SEND && isInputMode) {
                    // Send Input Mode
                    BasicTextField(
                        value = amount,
                        onValueChange = onAmountChange,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.End,
                            fontSize = 32.sp,
                            color = CardOnBackground
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (showQrScanner) {
                                    IconButton(onClick = onScanQr, modifier = Modifier.size(32.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.QrCodeScanner,
                                            contentDescription = "Scan QR code to input amount",
                                            tint = Color.Black
                                        )
                                    }
                                }
                                Box(Modifier.weight(1f)) { innerTextField() }
                            }
                        }
                    )
                    Text(
                        text = usdValue.orEmpty(),
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    // Selection Mode (Left) or Receive Display Mode (Right)
                    Text(
                        text = amount,
                        style = MaterialTheme.typography.titleLarge.copy(color = CardOnBackground),
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                    if (usdValue != null) {
                        Text(
                            text = usdValue,
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}