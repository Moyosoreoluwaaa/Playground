package com.playground

//                val mockNfts = remember {
//                    listOf(
//                        NftItem("url", "Cosmic Bloom", "Art Gems", "5.5", "ETH"),
//                        NftItem("url", "Ethernon Hills", "Doodles", "2.5", "ETH"),
//                        NftItem("url", "Ethereum", "CryptoKitty", "2.5", "ETH"),
//                        NftItem("url", "Metaverse Explorer", "Pixel Pals", "2.5", "ETH"),
//                        NftItem("url", "Aura of Time", "Digital Works", "1.1", "ETH"),
//                        NftItem("url", "Midnight City", "Neon Dreams", "3.0", "ETH"),
//                    ).mapIndexed { index, item ->
//                        item.copy(imageUrl = NFT_IMAGE_URLS[index % NFT_IMAGE_URLS.size])
//                    }
//                }
//
//                val previewState = NftUiState(
//                    NftScreenStateVal = NftScreenState.READY,
//                    selectedTab = NftTabType.COLLECTIBLES,
//                    collectibles = mockNfts,
//                    currentSelectedNftAppTab = NftAppTab.PORTFOLIO
//                )
//
//                SwapAppTheme {
//                    NftScreen(state = previewState, eventSink = {})
//                }
//                PlannerScreenContent(uiState = MockPlannerUiState)
//                TaskDetailScreen(uiState = MockTaskDetailUiState, onCloseClicked = {})