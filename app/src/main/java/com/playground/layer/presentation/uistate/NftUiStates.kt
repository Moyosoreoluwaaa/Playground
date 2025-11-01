package com.playground.layer.presentation.uistate

import androidx.compose.runtime.Immutable
import com.playground.layer.domain.model.NftAppTab
import com.playground.layer.domain.model.NftItem
import com.playground.layer.domain.model.NftScreenState
import com.playground.layer.domain.model.NftTabType

data class NftUiState(
    val NftScreenStateVal: NftScreenState = NftScreenState.LOADING,
    val selectedTab: NftTabType = NftTabType.COLLECTIBLES,
    val pagerStatePage: Int = 0,
    val collectibles: List<NftItem> = emptyList(),
    val activity: List<NftItem> = emptyList(),
    val currentSelectedNftAppTab: NftAppTab = NftAppTab.PORTFOLIO,
    val isFetchingMore: Boolean = false
)

sealed class NftEvent {
    data object onBackClicked : NftEvent()
    data class onTabSelected(val tab: NftTabType) : NftEvent()
    data class onPagerSwipe(val pageIndex: Int) : NftEvent()
    data class onNftClicked(val nft: NftItem) : NftEvent()
    data object onLoadMoreNfts : NftEvent()
    data class onNftAppTabSelected(val tab: NftAppTab) : NftEvent()
}
