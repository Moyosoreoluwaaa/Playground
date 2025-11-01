package com.playground.layer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.playground.layer.domain.model.NftItem
import com.playground.layer.domain.model.NftTabType
import com.playground.layer.presentation.uistate.NftEvent

@Composable
fun NftTabContent(tabType: NftTabType, nftItems: List<NftItem>, eventSink: (NftEvent) -> Unit, modifier: Modifier = Modifier) {
    if (tabType == NftTabType.COLLECTIBLES) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(nftItems.size) {
                NFTCard(
                    nft = nftItems[it],
                    onClick = { nftItem -> eventSink(NftEvent.onNftClicked(nftItem)) }
                )
            }
        }
    } else {
        Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("Activity Feed Coming Soon...", color = Color.Gray)
        }
    }
}