package com.playground.tempo.presentation.uistate

import com.playground.tempo.domain.model.TempoBanner
import com.playground.tempo.domain.model.TempoProduct

data class TempoDashboardUiState(
    val banners: List<TempoBanner>,
    val newInProducts: List<TempoProduct>,
    val discoverProducts: List<TempoProduct>
)
