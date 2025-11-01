package com.playground.language.data

data class LangBadge(
    val id: String,
    val name: String,
    val icon: LangBadgeIcon
)

enum class LangBadgeIcon {
    STAR, // Word Master
    SPEAKER, // Grammar Guru
    TROPHY, // Listening Legend
    MOUNTAIN // Consistent Climber
}

data class LangStreakDay(
    val dayNumber: Int, // e.g., 1, 2, 3...
    val isActive: Boolean // Completed study goal on this day
)