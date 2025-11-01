package com.playground.diet.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.vector.ImageVector

enum class NonaNavRoute {
    HOME, GRID, SHARE, NOTIFICATIONS, PROFILE;
    val icon: ImageVector get() = when (this) {
        HOME -> Icons.Filled.Home; GRID -> Icons.Default.Apps; SHARE -> Icons.Default.Share; NOTIFICATIONS -> Icons.Default.Notifications; PROFILE -> Icons.Default.Person
    }
}
