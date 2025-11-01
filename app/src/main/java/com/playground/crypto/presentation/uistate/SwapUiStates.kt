package com.playground.crypto.presentation.uistate

import com.playground.crypto.domain.model.Currency

data class SwapUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val fromCurrency: Currency,
    val toCurrency: Currency,
    val fromAmount: String = "",
    val toAmount: String = "", // Typically derived
    val fromBalance: String,
    val fee: String = "",
    val isSwapButtonEnabled: Boolean = false
)
