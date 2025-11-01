package com.playground.layer.presentation.uistate

import com.playground.layer.domain.model.FeeLevel
import com.playground.layer.domain.model.ScreenState

data class SendUiState(
    val screenState: ScreenState = ScreenState.LOADING,

    // Address State
    val recipientAddress: String = "",
    val isAddressValid: Boolean = true,

    // Amount State
    val sendAssetTicker: String = "BTC",
    val sendAmount: String = "",
    val sendAmountFiatValue: String = "$0.00 TC", // BTC amount converted to fiat
    val impliedFeeDetail: String = "", // e.g., (-$0.00 USD)

    // Fee State
    val selectedFeeLevel: FeeLevel = FeeLevel.SLOW_AVG,
    val networkFeeAmountBTC: String = "0.0000 BTC",
    val networkFeeAmountFiat: String = "0.00",
    val totalSendAmountBTC: String = "0.0000 BTC", // sendAmount + networkFeeAmountBTC
    val availableBalanceBTC: Double = 0.0,

    // UI Controls
    val isReviewButtonEnabled: Boolean = false
)

sealed class SendEvent {
    data object onBackClicked : SendEvent()
    data class onAddressChange(val address: String) : SendEvent()
    data object onScanQrCode : SendEvent()
    data class onAmountChange(val amount: String) : SendEvent()
    data object onFiatCardClicked : SendEvent()
    data class onFeeLevelChanged(val level: FeeLevel) : SendEvent()
    data object onReviewAndSend : SendEvent()
}
