package com.playground.language

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.horizontalScroll


// ================================================================================
// 1. UTILS & DOMAIN MODELS
// ================================================================================

// Domain Models (Subset for the Onboarding screen)
@Immutable
data class LangMetric(val id: String, val value: String, val label: String) // Simplified
enum class LangGamifiedItemCategory { GRAMMAR, TRAVEL, VOCABULARY }
@Immutable
data class LangGamifiedMetricItem(
    val id: String,
    val title: String,
    val category: LangGamifiedItemCategory
) // Simplified

enum class LangMetricType { STREAK, TIME_SPENT, BADGE }
@Immutable
data class LangAchievementMetric(
    val id: String,
    val label: String,
    val value: String,
    val type: LangMetricType
)

enum class LangBadgeIcon { STAR, SPEAKER, TROPHY, MOUNTAIN }
@Immutable
data class LangBadge(val id: String, val name: String, val icon: LangBadgeIcon)
@Immutable
data class LangStreakDay(val dayNumber: Int, val isActive: Boolean)
@Immutable
data class LangAnswer(
    val id: String,
    val text: String,
    val foreignText: String? = null,
    val isCorrect: Boolean = false
)

@Immutable
data class LangGrammarOption(
    val id: String,
    val text: String,
    val completionPercentage: Int = 0,
    val isCorrect: Boolean = false
)

// Onboarding Models
@Immutable
data class LangLanguageOption(
    val code: String,
    val name: String,
    val icon: ImageVector
) // FIX: Replaced R_android with ImageVector

sealed class LangDailyGoal(val minutes: Int, val label: String) {
    data object FiveMin : LangDailyGoal(5, "5 min")
    data object FifteenMin : LangDailyGoal(15, "15 min")
    data object ThirtyMin : LangDailyGoal(30, "30 min")
    data object FortyFiveMin : LangDailyGoal(45, "45 min")
    data object OneHour : LangDailyGoal(60, "1 hr")
    data object TwoHour : LangDailyGoal(120, "2 hr")
    companion object {
        fun allGoals() = listOf(FiveMin, FifteenMin, ThirtyMin, FortyFiveMin, OneHour, TwoHour)
    }
}

