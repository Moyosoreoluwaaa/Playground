package com.playground.layer.data.source

import androidx.compose.runtime.remember
import com.playground.layer.domain.model.Asset
import com.playground.layer.domain.model.DApp
import com.playground.layer.domain.model.FeeLevel
import com.playground.layer.domain.model.HistoryEntry
import com.playground.layer.domain.model.NftAppTab
import com.playground.layer.domain.model.NftItem
import com.playground.layer.domain.model.NftScreenState
import com.playground.layer.domain.model.NftTabType
import com.playground.layer.domain.model.ScreenState
import com.playground.layer.domain.model.TabType
import com.playground.layer.domain.model.Token
import com.playground.layer.presentation.uistate.NftUiState
import com.playground.layer.presentation.uistate.PortfolioOverviewUiState
import com.playground.layer.presentation.uistate.SwapUiState

// --- Image URL Constants (Centralized) ---
const val GENERIC_DAPP_ICON_URL =
    "https://images.unsplash.com/photo-1477959858617-67f85cf4f1df" // Technology
const val GENERIC_ASSET_ICON_URL =
    "https://images.unsplash.com/photo-1506744038136-46273834b3fb" // Landscape

// NFT Image Pool (Used for NFTCard previews)
val NFT_IMAGE_URLS = listOf(
    "https://images.unsplash.com/photo-1477959858617-67f85cf4f1df", // Nature 1
    "https://images.unsplash.com/photo-1477959858617-67f85cf4f1df", // City 2
    "https://images.unsplash.com/photo-1477959858617-67f85cf4f1df", // Tech 3
    "https://images.unsplash.com/photo-1477959858617-67f85cf4f1df", // Nature 3
    "https://images.unsplash.com/photo-1477959858617-67f85cf4f1df", // City 1
    "https://images.unsplash.com/photo-1477959858617-67f85cf4f1df", // Tech 2
)

// Mock Data
val MockTokenList = listOf(
    Token(
        id = "BTC",
        name = "Bitcoin",
        ticker = "BTC",
        iconUrl = "https://example.com/btc.png",
        balanceTicker = "09 BTC",
        secondaryLabel = "Balsance",
        priceSecondaryLabel = "$15 Feb.00",
        percentageChange = 4.5,
        isPositive = true
    ),
    Token(
        id = "ETH",
        name = "Ethereum",
        ticker = "ETH",
        iconUrl = "https://example.com/eth.png",
        balanceTicker = "0.5 ETH",
        secondaryLabel = "ETH",
        priceSecondaryLabel = "$2.5k Feb.00",
        percentageChange = 5.1,
        isPositive = true
    ),
    Token(
        id = "ETH2",
        name = "ETH",
        ticker = "ETH2",
        iconUrl = "https://example.com/eth2.png",
        balanceTicker = "15 farem",
        secondaryLabel = "15 farem",
        priceSecondaryLabel = "$2.5k Feb.00",
        percentageChange = 8.3,
        isPositive = true
    ),
    Token(
        id = "ADA",
        name = "Cardana",
        ticker = "ADA",
        iconUrl = "https://example.com/ada.png",
        balanceTicker = "500 ADA",
        secondaryLabel = "ADA",
        priceSecondaryLabel = "$0.48 Feb.00",
        percentageChange = -0.8,
        isPositive = false
    )
)

val MockUiState = PortfolioOverviewUiState(
    tokenList = MockTokenList
)

// Mock/Sample UiState
val MockSwapUiStateInput = SwapUiState(
    screenState = ScreenState.READY,
    sendAsset = Asset(ticker = "BTC", name = "Bitcoin", iconUrl = "https://upload.wikimedia.org/wikipedia/commons/4/46/Bitcoin.svg"),
    sendAmountInput = "0.5",
    sendAmountUsdValue = "$33,725.10 USD",
    isSendInputFocused = true,
    receiveAsset = Asset(ticker = "ETH", name = "Ethereum", iconUrl = "https://upload.wikimedia.org/wikipedia/commons/6/6f/Ethereum-icon-purple.svg"),
    receiveAmountDisplay = "15.2 ETH",
    receiveAmountUsdValue = "$30,400 USD",
    rate = "Rate: 1 BTC = 30.4 ETH",
    networkFee = "0.0002 BTC",
    feeLevel = FeeLevel.AVG,
    feeAssetTicker = "ETH",
    isReviewButtonEnabled = true,
    isSwapping = false
)

// Mock/Sample UiState
val MockSwapUiState = SwapUiState(
    screenState = ScreenState.READY,
    sendAmountInput = "0.5",
    sendAmountUsdValue = "$33,725.10 USD",
    receiveAmountDisplay = "15.2 ETH",
    receiveAmountUsdValue = "$30,400 USD",
    rate = "1 BTC = 30.4 ETH",
    networkFee = "Slow = Avg ETH",
    feeAssetTicker = "ETH",
    isReviewButtonEnabled = true,
    isSendInputFocused = true
)

// Generate mock data for the preview
val mockNfts = listOf(
    NftItem("url", "Cosmic Bloom", "Art Gems", "5.5", "ETH"),
    NftItem("url", "Ethernon Hills", "Doodles", "2.5", "ETH"),
    NftItem("url", "Ethereum", "CryptoKitty", "2.5", "ETH"),
    NftItem("url", "Metaverse Explorer", "Pixel Pals", "2.5", "ETH"),
    NftItem("url", "Aura of Time", "Digital Works", "1.1", "ETH"),
    NftItem("url", "Midnight City", "Neon Dreams", "3.0", "ETH"),
).mapIndexed { index, item ->
    item.copy(imageUrl = NFT_IMAGE_URLS[index % NFT_IMAGE_URLS.size])
}
