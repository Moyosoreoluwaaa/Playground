package com.playground.layer.presentation.uistate

import com.playground.layer.domain.model.Asset
import com.playground.layer.domain.model.FeeLevel
import com.playground.layer.domain.model.ScreenState
import com.playground.layer.domain.model.SwapType

data class SwapUiState(
    val screenState: ScreenState = ScreenState.LOADING,

    // Send Asset State
    val sendAsset: Asset = Asset(ticker = "BTC", name = "Bitcoin", iconUrl = "bitcoin_icon_url"),
    val sendAmountInput: String = "",
    val sendAmountUsdValue: String = "$0.00 USD",
    val isSendInputFocused: Boolean = false, // Controls input mode appearance

    // Receive Asset State
    val receiveAsset: Asset = Asset(ticker = "ETH", name = "Ethereum", iconUrl = "ethereum_icon_url"),
    val receiveAmountDisplay: String = "0.0 ETH",
    val receiveAmountUsdValue: String = "$0.00 USD",

    // Details State
    val rate: String? = null, // e.g., "1 BTC = 30.4 ETH"
    val networkFee: String = "0.0000 BTC",
    val feeLevel: FeeLevel = FeeLevel.AVG,
    val feeAssetTicker: String = "BTC", // Ticker for network fee

    // UI Controls
    val isReviewButtonEnabled: Boolean = false,
    val isSwapping: Boolean = false // For swap animation state
)
sealed class SwapEvent {
    data object onSendAssetClick : SwapEvent()
    data object onReceiveAssetClick : SwapEvent()
    data object onSwapAssets : SwapEvent()
    data class onSendAmountChange(val amount: String) : SwapEvent()
    data object onScanQrCode : SwapEvent()
    data object onFeeLevelSelect : SwapEvent()
    data object onReviewSwap : SwapEvent()
}
