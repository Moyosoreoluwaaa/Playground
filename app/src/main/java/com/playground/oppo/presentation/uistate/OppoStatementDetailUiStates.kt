package com.playground.oppo.presentation.uistate

import androidx.compose.ui.graphics.Color
import com.playground.oppo.domain.model.ActivityItemData

data class OppoStatementDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val screenTitle: String = "My Earning",
    val totalEarning: Double = 45670.0,
    val dateRange: String = "28 May – 04 June",
    val sectionTitle: String = "Sign Up",
    val activityList: List<ActivityItemData>
) {
    companion object {
        val OrangeIcon = Color(0xFFFF4500)
        val YellowGreenIcon = Color(0xFFD5F3D1) // Adjusted for light green/yellow from image

        fun mock() = OppoStatementDetailUiState(
            activityList = listOf(
                ActivityItemData("organic_1", "Organic", 1, OrangeIcon, "^8.50%", "$20,120", "4 Mins Ago"),
                ActivityItemData("social_1", "Social Ads", 2, YellowGreenIcon, "^43.50%", "$20,150", "2 Mins Ago"),
                ActivityItemData("referral_1", "Referrals", 3, Color(0xFFB064A2), "^12.00%", "$5,400", "1 Hour Ago"),
                ActivityItemData("paid_1", "Paid Campaigns", 4, OrangeIcon, "^5.00%", "$10,000", "1 Day Ago"),
            )
        )
    }
}

sealed class OppoStatementDetailEvent {
    data object OnMenuClick : OppoStatementDetailEvent()
    data object OnDateRangeClick : OppoStatementDetailEvent()
    data object OnMyAccountClick : OppoStatementDetailEvent()
    data object OnSettingsClick : OppoStatementDetailEvent()
    data object OnMaximizeClick : OppoStatementDetailEvent()
    data class OnActivityClick(val activityId: String) : OppoStatementDetailEvent()
}
