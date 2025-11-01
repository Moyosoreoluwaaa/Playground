package com.playground.healwise.presentation.uistate

import com.playground.healwise.domain.model.SingleMetricData
import com.playground.healwise.domain.model.StackedMetricData

data class MaxHealedUiState(
    val isVerified: Boolean = true,
    val filterTag: String = "All Patients",
    val mainMetricValue: String = "1.2M",
    val mainMetricDescription: String = "Total Active Patients",
    val liveDataLabel: String = "Live Data",
    val activeDoctorsCard: StackedMetricData,
    val hospitalsCard: StackedMetricData,
    val reAdmittedCard: SingleMetricData,
    val occupancyCard: SingleMetricData,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface MaxHealedEvent {
    data object OnMenuClick : MaxHealedEvent
    data object OnFilterTagClick : MaxHealedEvent
    data object OnActiveDoctorsClick : MaxHealedEvent
    data object OnHospitalsClick : MaxHealedEvent
    data object OnReAdmittedClick : MaxHealedEvent
    data object OnOccupancyClick : MaxHealedEvent
}
