package com.playground.healwise.presentation.uistate

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.ui.graphics.vector.ImageVector
import com.playground.healwise.domain.model.PatientStats
import com.playground.healwise.domain.model.ServiceMetricCardData

// --- Screen UiState (From Section 7) ---
data class DashboardUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val premiumLabel: String = "Premium",
    val premiumIcon: ImageVector = Icons.Default.FlashOn,
    val growthPercentage: String,
    val growthDescription: String = "Growth Performed Last Week",
    val packageCard: ServiceMetricCardData,
    val labResultsCard: ServiceMetricCardData,
    val patientStats: PatientStats
)

// --- Events (From Section 9) ---
sealed class DashboardEvent {
    data object OnMenuToggled : DashboardEvent()
    data object OnPeriodFilterClick : DashboardEvent()
    data object OnPackageCardClick : DashboardEvent()
    data object OnLabResultsCardClick : DashboardEvent()
    data object OnPatientStatsDetailsClick : DashboardEvent()
}
