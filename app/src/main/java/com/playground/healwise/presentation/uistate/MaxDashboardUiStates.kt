package com.playground.healwise.presentation.uistate

import com.playground.healwise.domain.model.MaxMetricCardData

data class MaxDashboardUiState(
    val isLoading: Boolean = false,
    val isVerified: Boolean = true,
    val title: String = "Max Patient Data ~",
    val subtitle: String = "Overview",
    val mainMetricLabel: String = "Average\nPatient Enroll\nDaily", // Multiline text
    val mainMetricValue: String = "+82^",
    val patientsCard: MaxMetricCardData,
    val familiesCard: MaxMetricCardData
)

sealed class MaxDashboardEvent {
    data object OnMenuClick : MaxDashboardEvent()
    data object OnPatientsCardClick : MaxDashboardEvent()
    data object OnFamiliesCardClick : MaxDashboardEvent()
}