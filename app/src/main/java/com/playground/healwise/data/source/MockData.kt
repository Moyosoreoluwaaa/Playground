package com.playground.healwise.data.source

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Science
import androidx.compose.ui.graphics.Color
import com.playground.healwise.domain.model.DiagramGroup
import com.playground.healwise.domain.model.PatientStats
import com.playground.healwise.domain.model.ServiceMetricCardData
import com.playground.healwise.presentation.uistate.DashboardUiState

// --- Mocks (Updated/Corrected) ---
val MockPatientStats = PatientStats(
    title = "Active Patients",
    groupA = DiagramGroup(
        valueFormatted = "1.2M",
        floatValue = 1.2f,
        color = Color(0xFF386641) // Dark Green
    ),
    groupB = DiagramGroup(
        valueFormatted = "0.8M",
        floatValue = 0.8f,
        color = Color(0xFF90A955) // Light Green
    )
)

val MockUiState = DashboardUiState(
    growthPercentage = "48.2%",
    packageCard = ServiceMetricCardData(
        title = "Semaglutide Injection Weekly 2",
        metricValue = "$32",
        subtitle = "Per Week Package",
        leadingIcon = Icons.Default.LocalPharmacy,
        metricTrailingIcon = Icons.Default.Lock,
        backgroundColor = Color(0xFFF7A97A)
    ),
    labResultsCard = ServiceMetricCardData(
        title = "Instant Lab Results Available",
        metricValue = "16^",
        subtitle = "See Clients Story",
        leadingIcon = Icons.Default.Science,
        metricTrailingIcon = Icons.Default.BarChart,
        backgroundColor = Color(0xFFC099E3)
    ),
    patientStats = MockPatientStats
)
