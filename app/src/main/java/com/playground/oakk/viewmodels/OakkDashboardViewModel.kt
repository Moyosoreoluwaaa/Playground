package com.playground.oakk.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.playground.oakk.uistates.OakkAssetCategory
import com.playground.oakk.uistates.OakkDashboardUiState
import com.playground.oakk.uistates.OakkMonthData
import com.playground.oakk.uistates.OakkSegment
import com.playground.oakk.uistates.TabType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OakkDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OakkDashboardUiState(isLoading = true))
    val uiState: StateFlow<OakkDashboardUiState> = _uiState.asStateFlow()

    init {
        // Simulate data fetching
        _uiState.update {
            it.copy(
                userName = "Alexandr Kosov",
                netWorth = "SAR 600,200.88",
                netWorthChange = "Going up 13% this month, good job",
                monthlyData = getMockMonthlyData(),
                assetCategories = getMockAssetCategories(),
                isLoading = false
            )
        }
    }

    fun onTabSelected(tab: TabType) {
        _uiState.update { it.copy(selectedTab = tab) }
        // Potentially load new data based on the selected tab
    }

    //region Mocks
    private fun getMockMonthlyData(): List<OakkMonthData> {
        val categories = getMockAssetCategories()
        val random = java.util.Random()
        return listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC").map { month ->
            OakkMonthData(
                month = month,
                segments = categories.map { category ->
                    OakkSegment(
                        categoryName = category.name,
                        value = random.nextDouble() * 100, // Mocked values
                        color = category.color
                    )
                }
            )
        }
    }

    private fun getMockAssetCategories(): List<OakkAssetCategory> {
        return listOf(
            OakkAssetCategory("Real Estate", "42%", Color(0xFF6A605A)),
            OakkAssetCategory("Accounts", "16%", Color(0xFFB1A7A0)),
            OakkAssetCategory("Cars", "15%", Color(0xFF566E44)),
            OakkAssetCategory("Crypto", "14%", Color(0xFFD6C8C1)),
            OakkAssetCategory("Stocks", "8%", Color(0xFF908E88)),
            OakkAssetCategory("Others", "3%", Color(0xFFDB8053))
        )
    }
    //endregion
}