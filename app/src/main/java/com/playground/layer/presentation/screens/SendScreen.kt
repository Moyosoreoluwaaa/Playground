package com.playground.layer.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.layer.domain.model.FeeLevel
import com.playground.layer.domain.model.ScreenState
import com.playground.layer.presentation.components.AmountFiatDisplay
import com.playground.layer.presentation.components.AmountInputCard
import com.playground.layer.presentation.components.FeeLevelSelector
import com.playground.layer.presentation.components.RecipientAddressInput
import com.playground.layer.presentation.components.ReviewButton
import com.playground.layer.presentation.theme.SwapAppTheme
import com.playground.layer.presentation.uistate.SendEvent
import com.playground.layer.presentation.uistate.SendUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendScreen(
    state: SendUiState,
    eventSink: (SendEvent) -> Unit
) {
    SwapAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { eventSink(SendEvent.onBackClicked) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                            )
                        }
                    },
                    title = { Text("Send Bitcoin", style = MaterialTheme.typography.titleLarge) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                item {
                    Text(
                        "Select Transfer Details",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    // Recipient Address
                    Text(
                        "Recipient Address",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    RecipientAddressInput(
                        address = if (state.recipientAddress.isEmpty()) "bc1q...." else state.recipientAddress,
                        onAddressChange = { eventSink(SendEvent.onAddressChange(it)) },
                        onScanQr = { eventSink(SendEvent.onScanQrCode) },
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Amount (BTC)
                    Text(
                        "Amount (${state.sendAssetTicker})",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AmountInputCard(
                            amount = state.sendAmount,
                            assetTicker = state.sendAssetTicker,
                            onAmountChange = { eventSink(SendEvent.onAmountChange(it)) },
                            modifier = Modifier
                                .weight(1.5f)
                                .clickable { eventSink(SendEvent.onFiatCardClicked) }
                        )
                        AmountFiatDisplay(
                            fiatValue = state.sendAmountFiatValue,
                            modifier = Modifier
                                .weight(2f)
                                .clickable { eventSink(SendEvent.onFiatCardClicked) }
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End){
                        Text(
                            state.impliedFeeDetail,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.Red,
                                textAlign = TextAlign.Right
                            ),
                            maxLines = 1
                        )
                    }
                    Spacer(Modifier.height(32.dp))


                    // Network Fee
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Network Fee",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        FeeLevelSelector(
                            currentFeeLevel = state.selectedFeeLevel,
                            onFeeLevelChange = { eventSink(SendEvent.onFeeLevelChanged(it)) }
                        )
                    }

                    // Fee Value
                    Text(
                        text = "${state.networkFeeAmountBTC} (${state.networkFeeAmountFiat})",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
                    )

                    // Action Button
                    ReviewButton(
                        isEnabled = state.isReviewButtonEnabled,
                        onClick = { eventSink(SendEvent.onReviewAndSend) },
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun SendScreenPreview() {
    val previewState = SendUiState(
        screenState = ScreenState.READY,
        recipientAddress = "bc1q....",
        sendAmount = "0.1",
        sendAmountFiatValue = "$6,745.02",
        impliedFeeDetail = "(-$6.74 USD)",
        selectedFeeLevel = FeeLevel.FAST,
        networkFeeAmountBTC = "0.1001 BTC",
        networkFeeAmountFiat = "6,751.76",
        isReviewButtonEnabled = true
    )
    SendScreen(state = previewState, eventSink = {})
}