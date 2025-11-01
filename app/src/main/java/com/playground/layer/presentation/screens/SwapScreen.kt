package com.playground.layer.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playground.layer.domain.model.SwapType
import com.playground.layer.presentation.components.CurrencyField
import com.playground.layer.presentation.components.DetailRow
import com.playground.layer.presentation.components.ReviewButton
import com.playground.layer.presentation.components.SwapActionButton
import com.playground.layer.presentation.theme.SwapAppTheme
import com.playground.layer.presentation.uistate.SwapEvent
import com.playground.layer.presentation.uistate.SwapUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapScreen(
    state: SwapUiState,
    eventSink: (SwapEvent) -> Unit
) {
    SwapAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Swap",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) {paddingValues ->
            // Use LazyColumn for potential scrollability of the screen content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                item {
                    // 1. Instruction Text
                    Text(
                        text = "Select assets to swap",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    // 2. "You Send" Section
                    Text(
                        text = "You send",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val sendAmountDisplay = if (state.isSendInputFocused) state.sendAmountInput else state.sendAsset.name
                    val sendUsdDisplay = if (state.isSendInputFocused) state.sendAmountUsdValue else state.sendAsset.ticker

                    CurrencyField(
                        type = SwapType.SEND,
                        asset = state.sendAsset,
                        amount = sendAmountDisplay,
                        usdValue = sendUsdDisplay,
                        isInputMode = state.isSendInputFocused,
                        showQrScanner = true,
                        onAssetClick = { eventSink(SwapEvent.onSendAssetClick) },
                        onAmountChange = { eventSink(SwapEvent.onSendAmountChange(it)) },
                        onScanQr = { eventSink(SwapEvent.onScanQrCode) }
                    )

                    // 3. Swap Action Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SwapActionButton(
                            isSwapping = state.isSwapping,
                            onClick = { eventSink(SwapEvent.onSwapAssets) }
                        )
                    }

                    // 4. "You Receive" Section
                    Text(
                        text = "You receive",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    CurrencyField(
                        type = SwapType.RECEIVE,
                        asset = state.receiveAsset,
                        amount = state.receiveAmountDisplay,
                        usdValue = state.receiveAmountUsdValue,
                        isInputMode = state.sendAmountInput.isNotEmpty(), // Input state dictates receive display
                        onAssetClick = { eventSink(SwapEvent.onReceiveAssetClick) }
                    )

                    Spacer(Modifier.height(32.dp))

                    // 5. Details Section
                    Text(
                        text = "Rate",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    DetailRow(
                        label = state.rate ?: "Rate",
                        value = "", // Rate value is the label itself in this layout interpretation
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val networkFeeValue = if (state.sendAmountInput.isEmpty()) {
                        "${state.networkFee}" // 0.0002 BTC (Left image state)
                    } else {
                        "${state.feeLevel.text} = ${state.feeLevel.text} ${state.feeAssetTicker}" // Slow = Avg ETH (Right image state)
                    }

                    DetailRow(
                        label = "Network Fee",
                        value = networkFeeValue,
                        onValueClick = { if (state.sendAmountInput.isNotEmpty()) eventSink(SwapEvent.onFeeLevelSelect) else null }
                    )

                    Spacer(Modifier.height(48.dp))
                }

                // 6. Action Button (Sticky/Fixed positioning usually, but placed at the bottom of the list for simplicity here)
                item {
                    ReviewButton(
                        isEnabled = state.isReviewButtonEnabled,
                        onClick = { eventSink(SwapEvent.onReviewSwap) },
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }
        }
    }
}