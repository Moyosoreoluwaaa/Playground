//package com.playground.layer
//
//import android.content.res.Configuration
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.darkColorScheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//
//// Mock/Sample Data
//private val mockTokenList = listOf(
//    // Using simple Int placeholders for icons since we don't have actual Drawables
//    TokenModel("1", "Bitcoin", "BTC", 0, 0.09, "0.09 BTC", 4.5, "Balsance"),
//    TokenModel("2", "Ethereum", "ETH", 1, 2.5, "2.5 ETH", 5.1, null),
//    TokenModel("3", "ETH", "ETH", 2, 15.0, "15.0 ETH", 8.3, "15 farem"),
//    TokenModel("4", "Cardano", "ADA", 3, 500.0, "500 ADA", -0.8, null),
//)
//
//private val mockUiState = PortfolioOverviewUiState(
//    totalBalance = "$5,832.50",
//    dailyChange = DailyChangeModel(4.5, "+4.5% Today"),
//    selectedTab = TabTypeLayer.TOKENS,
//    selectedBottomNav = BottomNavTabLayer.ACCOUNT,
//    tokenList = mockTokenList,
//    nftCount = 3
//)
//
//@Preview(
//    showSystemUi = true,
////    uiMode = Configuration.UI_MODE_NIGHT_YES
//)
//@Composable
//fun PortfolioOverviewScreenPreviewLayer() {
//    // Minimal Material 3 Theme definition for the preview to ensure colors and fonts work.
//    MaterialTheme(
//        colorScheme = darkColorScheme(
//            primary = PrimaryOrange,
//            onPrimary = Color.Black,
//            background = DarkBackground,
//            surface = CardSurface,
//            onSurface = Color.White,
//            surfaceContainer = CardSurface
//        )
//    ) {
//        PortfolioOverviewScreen(
//            uiState = mockUiState,
//            onEvent = {} // No actual event handling needed for the preview
//        )
//    }
//}