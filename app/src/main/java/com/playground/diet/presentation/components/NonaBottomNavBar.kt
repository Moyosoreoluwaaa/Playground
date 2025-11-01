package com.playground.diet.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.playground.diet.domain.model.NonaNavRoute

@Composable
fun NonaBottomNavBar(
    selectedRoute: NonaNavRoute,
    notificationCount: Int,
    onNavigate: (NonaNavRoute) -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NonaNavRoute.entries.forEach { route ->
                NonaBottomNavItem(
                    route = route,
                    isSelected = route == selectedRoute,
                    badgeCount = if (route == NonaNavRoute.NOTIFICATIONS) notificationCount else null,
                    onClick = onNavigate
                )
            }
        }
    }
}
