//package com.playground.language.domain
//
//import androidx.compose.runtime.Immutable
//import androidx.annotation.DrawableRes
//import androidx.compose.ui.graphics.vector.ImageVector
//
//// ================================================================================
//// I. DOMAIN MODELS (Unified)
//// ================================================================================
//
//
//@Immutable
//data class LangAchievementMetric(
//    val id: String,
//    val label: String,
//    val value: String,
//    val type: LangMetricType
//)
//
//enum class LangMetricType { STREAK, TIME_SPENT, BADGE }
//
//enum class LangBadgeIcon { STAR, SPEAKER, TROPHY, MOUNTAIN }
//
//@Immutable
//data class LangBadge(val id: String, val name: String, val icon: LangBadgeIcon)
//
//@Immutable
//data class LangStreakDay(val dayNumber: Int, val isActive: Boolean)
//
//// New Model for Onboarding
//@Immutable
//data class LangLanguageOption(val code: String, val name: String, val flagResId: Int)
//
//// The Time Goal is better represented as an Enum or sealed class of predefined goals.
//sealed class LangDailyGoal(val minutes: Int, val label: String) {
//    data object FiveMin : LangDailyGoal(5, "5 min")
//    data object FifteenMin : LangDailyGoal(15, "15 min")
//    data object ThirtyMin : LangDailyGoal(30, "30 min")
//    data object FortyFiveMin : LangDailyGoal(45, "45 min")
//    data object OneHour : LangDailyGoal(60, "1 hour")
//    data object TwoHour : LangDailyGoal(120, "2 hour")
//
//    // Use a list of goals derived from this.
//    companion object {
//        fun allGoals() = listOf(FiveMin, FifteenMin, ThirtyMin, FortyFiveMin, OneHour)
//        fun fromMinutes(minutes: Int) = allGoals().find { it.minutes == minutes } ?: ThirtyMin
//    }
//}
//
//
//// ================================================================================
//// II. UI STATE (New Screen)
//// ================================================================================
//
//@Immutable
//data class LangOnboardingUiState(
//    val isLoading: Boolean = false,
//    val headerText: String = "Welcome to Lingo!",
//    val instructionText: String = "Set your daily goal to get started",
//    val availableGoals: List<LangDailyGoal> = LangDailyGoal.allGoals(),
//    val selectedGoal: LangDailyGoal = LangDailyGoal.ThirtyMin,
//    val availableLanguages: List<LangLanguageOption> = emptyList(),
//    val selectedLanguageCode: String? = null,
//    val startLearningEnabled: Boolean = false,
//    val errorMessage: String? = null
//)
//
//// ================================================================================
//// III. UI EVENTS (New Screen)
//// ================================================================================
//
//sealed class LangOnboardingUiEvent {
//    data object OnBackClick : LangOnboardingUiEvent()
//    data object OnSkipClick : LangOnboardingUiEvent()
//    data object OnStartLearningClick : LangOnboardingUiEvent()
//    data class OnGoalSelected(val goal: LangDailyGoal) : LangOnboardingUiEvent()
//    data class OnLanguageSelected(val language: LangLanguageOption) : LangOnboardingUiEvent()
//}